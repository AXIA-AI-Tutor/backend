package com.ax.avatarcoach.domain.answer.service;

import com.ax.avatarcoach.domain.answer.dto.AnswerCreateRequest;
import com.ax.avatarcoach.domain.answer.dto.AnswerResponse;
import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.domain.answer.repository.AnswerRepository;
import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import com.ax.avatarcoach.domain.feedback.repository.FeedbackRepository;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.repository.SessionRepository;
import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.security.oauth.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

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
    public AnswerResponse createAnswer(AnswerCreateRequest request, OAuth2User oAuth2User) {
        validateDateRange(request);

        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findById(request.sessionId())
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

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

        return AnswerResponse.from(answerRepository.save(answer));
    }

    private void validateDateRange(AnswerCreateRequest request) {
        if (request.startedAt() != null && request.endedAt() != null
            && request.endedAt().isBefore(request.startedAt())) {
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
}
