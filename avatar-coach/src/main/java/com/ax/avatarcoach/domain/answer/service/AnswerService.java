package com.ax.avatarcoach.domain.answer.service;

import com.ax.avatarcoach.domain.answer.dto.*;
import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.domain.answer.entity.SttStatus;
import com.ax.avatarcoach.domain.answer.repository.AnswerRepository;
import com.ax.avatarcoach.domain.corpus.service.CorpusRagContextService;
import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import com.ax.avatarcoach.domain.feedback.dto.InternalFeedbackCreateRequest;
import com.ax.avatarcoach.domain.feedback.repository.FeedbackRepository;
import com.ax.avatarcoach.domain.feedback.service.FeedbackService;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionStatus;
import com.ax.avatarcoach.domain.session.repository.SessionRepository;
import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import com.ax.avatarcoach.global.ai.client.AiGatewayClient;
import com.ax.avatarcoach.global.ai.client.dto.AiRagContextItem;
import com.ax.avatarcoach.global.ai.client.dto.AiSttResponse;
import com.ax.avatarcoach.global.ai.client.dto.AiTurnRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiTurnResponse;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.security.oauth.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final FeedbackService feedbackService;
    private final AiGatewayClient aiGatewayClient;
    private final CorpusRagContextService corpusRagContextService;

    public List<AnswerResponse> getSessionAnswers(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        return answerRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId).stream()
            .map(AnswerResponse::from)
            .toList();
    }

    public AnswerResponse getAnswer(Long answerId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Answer answer = answerRepository.findByIdAndSessionUserId(answerId, user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        return AnswerResponse.from(answer);
    }

    public List<FeedbackResponse> getAnswerFeedbacks(Long answerId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        answerRepository.findByIdAndSessionUserId(answerId, user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        return feedbackRepository.findAllByAnswerIdOrderByCreatedAtAsc(answerId).stream()
            .map(FeedbackResponse::from)
            .toList();
    }

    @Transactional
    public AnswerResponse createAnswerForUser(AnswerCreateRequest request, OAuth2User oAuth2User) {
        validateDateRange(request.startedAt(), request.endedAt());

        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findById(request.sessionId())
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        return createAnswerInternal(session.getId(), new InternalAnswerCreateRequest(
            request.questionText(), request.transcript(), request.durationSec(), request.speechRate(),
            request.silenceCount(), request.fillerWordCount(), request.eyeContactScore(), request.postureScore(),
            request.sttStatus(), request.startedAt(), request.endedAt()
        ));
    }

    @Transactional
    public AnswerWithFeedbackResponse submitAnswerWithFeedback(
        Long sessionId,
        AnswerSubmitRequest request,
        OAuth2User oAuth2User
    ) {
        validateDateRange(request.startedAt(), request.endedAt());

        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Answer answer = createAnswerEntityInternal(session.getId(), new InternalAnswerCreateRequest(
            request.questionText(),
            null,
            null,
            null,
            null,
            null,
            request.eyeContactScore(),
            request.postureScore(),
            SttStatus.PENDING,
            request.startedAt(),
            request.endedAt()
        ));

        String visionMetricsJson = """
            {"eye_contact_score":%d,"posture_score":%d}
            """.formatted(
            request.eyeContactScore(),
            request.postureScore()
        );

        AiSttResponse sttResponse = aiGatewayClient.transcribe(request.file());
        answer.completeStt(sttResponse.transcript());

        List<AiRagContextItem> ragContext = corpusRagContextService.buildTurnRagContext(
            session,
            answer.getQuestionText(),
            sttResponse.transcript()
        );

        AiTurnRequest aiRequest = new AiTurnRequest(
            user.getId(),
            session.getId(),
            answer.getId(),
            session.getMode().name(),
            answer.getQuestionText(),
            sttResponse.transcript(),
            visionMetricsJson,
            ragContext
        );

        AiTurnResponse aiFeedback = aiGatewayClient.evaluateTurn(aiRequest);

        answer.completeStt(aiFeedback.transcript());

        FeedbackResponse feedback = feedbackService.createFeedbackInternal(
            answer.getId(),
            new InternalFeedbackCreateRequest(
                aiFeedback.summary(),
                aiFeedback.evidence(),
                aiFeedback.improvementExample(),
                aiFeedback.ttsAudioUrl(),
                aiFeedback.structureScore(),
                aiFeedback.specificityScore(),
                aiFeedback.relevanceScore(),
                aiFeedback.deliveryScore()
            )
        );

        return AnswerWithFeedbackResponse.of(AnswerResponse.from(answer), feedback);
    }

    @Transactional
    public AnswerResponse createAnswerInternal(Long sessionId, InternalAnswerCreateRequest request) {
        return AnswerResponse.from(createAnswerEntityInternal(sessionId, request));
    }

    private void validateDateRange(java.time.LocalDateTime startedAt, java.time.LocalDateTime endedAt) {
        if (startedAt != null && endedAt != null && endedAt.isBefore(startedAt)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private User getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        String providerUserId = userInfo.getProviderUserId();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, providerUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Answer createAnswerEntityInternal(Long sessionId, InternalAnswerCreateRequest request) {
        validateDateRange(request.startedAt(), request.endedAt());

        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        Answer answer = Answer.create(
            session,
            request.questionText(),
            request.transcript(),
            request.durationSec(),
            request.speechRate(),
            request.silenceCount(),
            request.fillerWordCount(),
            request.eyeContactScore(),
            request.postureScore(),
            request.sttStatus(),
            request.startedAt(),
            request.endedAt()
        );

        return answerRepository.save(answer);
    }
}
