package com.ax.avatarcoach.domain.feedback.service;

import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.domain.answer.repository.AnswerRepository;
import com.ax.avatarcoach.domain.feedback.dto.FeedbackCreateRequest;
import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import com.ax.avatarcoach.domain.feedback.dto.InternalFeedbackCreateRequest;
import com.ax.avatarcoach.domain.feedback.entity.Feedback;
import com.ax.avatarcoach.domain.feedback.repository.FeedbackRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedbackResponse createFeedbackForUser(FeedbackCreateRequest request, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Answer answer = answerRepository.findById(request.answerId())
            .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        if (!answer.getSession().getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ANSWER_ACCESS_DENIED);
        }

        return createFeedbackInternal(answer.getId(), new InternalFeedbackCreateRequest(
            request.summary(), request.evidence(), request.improvementExample(), request.structureScore(),
            request.specificityScore(), request.relevanceScore(), request.deliveryScore()
        ));
    }

    @Transactional
    public FeedbackResponse createFeedbackInternal(Long answerId, InternalFeedbackCreateRequest request) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        validateRequest(request.summary(), request.structureScore(), request.specificityScore(), request.relevanceScore(), request.deliveryScore());

        Feedback feedback = Feedback.create(
            answer,
            request.summary(),
            request.evidence(),
            request.improvementExample(),
            request.structureScore(),
            request.specificityScore(),
            request.relevanceScore(),
            request.deliveryScore()
        );

        return FeedbackResponse.from(feedbackRepository.save(feedback));
    }

    private void validateRequest(String summary, Integer structureScore, Integer specificityScore, Integer relevanceScore, Integer deliveryScore) {
        if (summary == null || summary.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        validateScoreRange(structureScore);
        validateScoreRange(specificityScore);
        validateScoreRange(relevanceScore);
        validateScoreRange(deliveryScore);
    }

    private void validateScoreRange(Integer score) {
        if (score != null && (score < 0 || score > 100)) {
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
