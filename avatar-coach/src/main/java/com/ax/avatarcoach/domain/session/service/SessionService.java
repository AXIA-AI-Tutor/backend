package com.ax.avatarcoach.domain.session.service;

import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.domain.answer.repository.AnswerRepository;
import com.ax.avatarcoach.domain.corpus.service.CorpusRagContextService;
import com.ax.avatarcoach.domain.document.entity.DocumentStatus;
import com.ax.avatarcoach.domain.document.repository.DocumentRepository;
import com.ax.avatarcoach.domain.feedback.entity.Feedback;
import com.ax.avatarcoach.domain.feedback.repository.FeedbackRepository;
import com.ax.avatarcoach.domain.session.dto.*;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionEventType;
import com.ax.avatarcoach.domain.session.entity.SessionStatus;
import com.ax.avatarcoach.domain.session.repository.SessionRepository;
import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import com.ax.avatarcoach.global.ai.client.AiGatewayClient;
import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateResponse;
import com.ax.avatarcoach.global.ai.client.dto.AiRagContextItem;
import com.ax.avatarcoach.global.ai.service.AiWarmupService;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.security.oauth.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.ObjectProvider;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionEventService sessionEventService;
    private final DocumentRepository documentRepository;
    private final AiGatewayClient aiGatewayClient;
    private final AiWarmupService aiWarmupService;
    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final ObjectProvider<CorpusRagContextService> corpusRagContextServiceProvider;

    private static final int MAX_QUESTION_COUNT = 4;

    @Transactional
    public SessionResponse createSession(OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = Session.create(user);

        Session savedSession = sessionRepository.save(session);

        sessionEventService.recordEvent(
            savedSession,
            SessionEventType.SESSION_CREATED,
            null
        );

        aiWarmupService.warmupForSessionCreate();

        return SessionResponse.from(savedSession);
    }

    public SessionResponse getSession(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        return SessionResponse.from(session);
    }

    public List<SessionResponse> getMySessions(OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        return sessionRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
            .map(SessionResponse::from)
            .toList();
    }

    @Transactional
    public SessionStartResponse startSession(
        Long sessionId,
        SessionStartRequest request,
        OAuth2User oAuth2User
    ) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        boolean hasReadyDocument = documentRepository.existsBySessionIdAndUserIdAndStatus(
            sessionId,
            user.getId(),
            DocumentStatus.READY_FOR_AI
        );

        if (!hasReadyDocument) {
            throw new CustomException(ErrorCode.SESSION_DOCUMENT_REQUIRED);
        }

        session.start(
            request.mode(),
            request.target(),
            request.difficulty()
        );

        sessionEventService.recordEvent(
            session,
            SessionEventType.SESSION_STARTED,
            null
        );

        AiQuestionGenerateRequest aiRequest = new AiQuestionGenerateRequest(
            user.getId(),
            session.getId(),
            session.getMode().name(),
            session.getTarget().name(),
            session.getDifficulty().name(),
            session.getAnswerTimeLimitSec(),
            1,
            List.of(),
            List.of(),
            List.of()
        );

        AiQuestionGenerateResponse aiQuestion = aiGatewayClient.generateQuestion(aiRequest);

        return SessionStartResponse.of(
            SessionResponse.from(session),
            aiQuestion
        );
    }

    @Transactional
    public SessionNextQuestionResponse generateNextQuestion(
        Long sessionId,
        OAuth2User oAuth2User
    ) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        List<Answer> answers = answerRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId);
        int nextQuestionIndex = answers.size() + 1;

        List<String> previousQuestions = answers.stream()
            .map(Answer::getQuestionText)
            .filter(questionText -> questionText != null && !questionText.isBlank())
            .toList();

        List<AiQuestionGenerateRequest.PreviousTurn> previousTurns = new ArrayList<>();

        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            Feedback feedback = feedbackRepository.findAllByAnswerIdOrderByCreatedAtAsc(answer.getId())
                .stream()
                .findFirst() // 사용 이유 : 현재 구조상 답변 하나에 피드백이 보통 1개지만 repository는 list를 반환
                .orElse(null);

            previousTurns.add(new AiQuestionGenerateRequest.PreviousTurn(
                answer.getId(),
                i + 1,
                answer.getQuestionText(),
                answer.getTranscript(),
                feedback != null ? feedback.getSummary() : null,
                feedback != null ? feedback.getImprovementExample() : null
            ));
        }

        // 1번 질문은 start가 담당하니까, questions/next API가 1번 질문을 생성하면 안됨
        if (nextQuestionIndex < 2 || nextQuestionIndex > MAX_QUESTION_COUNT) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        String ragQuery = buildRagQuery(previousTurns);

        CorpusRagContextService corpusRagContextService =
            corpusRagContextServiceProvider.getIfAvailable();

        List<AiRagContextItem> ragContext =
            corpusRagContextService == null
                ? List.of()
                : corpusRagContextService.buildQuestionRagContext(session, ragQuery);

        AiQuestionGenerateRequest aiRequest = new AiQuestionGenerateRequest(
            user.getId(),
            session.getId(),
            session.getMode().name(),
            session.getTarget().name(),
            session.getDifficulty().name(),
            session.getAnswerTimeLimitSec(),
            nextQuestionIndex,
            previousQuestions,
            previousTurns,
            ragContext
        );

        AiQuestionGenerateResponse aiQuestion = aiGatewayClient.generateQuestion(aiRequest);

        return SessionNextQuestionResponse.of(
            session.getId(),
            nextQuestionIndex,
            MAX_QUESTION_COUNT,
            aiQuestion
        );
    }

    @Transactional
    public SessionResponse completeSession(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        session.complete();

        sessionEventService.recordEvent(
            session,
            SessionEventType.SESSION_COMPLETED,
            null
        );

        return SessionResponse.from(session);
    }

    public List<SessionEventResponse> getSessionEvents(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        return sessionEventService.getSessionEvents(session);
    }

    private String buildRagQuery(List<AiQuestionGenerateRequest.PreviousTurn> previousTurns) {
        if (previousTurns == null || previousTurns.isEmpty()) {
            return "";
        }

        AiQuestionGenerateRequest.PreviousTurn lastTurn = previousTurns.get(previousTurns.size() - 1);

        return String.join(" ",
            nullToEmpty(lastTurn.questionText()),
            nullToEmpty(lastTurn.transcript()),
            nullToEmpty(lastTurn.feedbackSummary()),
            nullToEmpty(lastTurn.improvementExample())
        ).trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private User getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        String providerUserId = userInfo.getProviderUserId();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE,
                providerUserId
            )
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
    }
}
