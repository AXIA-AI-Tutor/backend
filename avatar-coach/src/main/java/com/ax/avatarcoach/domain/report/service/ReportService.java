package com.ax.avatarcoach.domain.report.service;

import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.domain.answer.repository.AnswerRepository;
import com.ax.avatarcoach.domain.feedback.repository.FeedbackRepository;
import com.ax.avatarcoach.domain.report.dto.ReportResponse;
import com.ax.avatarcoach.domain.report.entity.Report;
import com.ax.avatarcoach.domain.report.repository.ReportRepository;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionStatus;
import com.ax.avatarcoach.domain.session.repository.SessionRepository;
import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import com.ax.avatarcoach.global.ai.client.AiGatewayClient;
import com.ax.avatarcoach.global.ai.client.dto.AiReportGenerateRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiReportGenerateResponse;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.security.oauth.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final AiGatewayClient aiGatewayClient;

    public ReportResponse getSessionReport(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        Report report = reportRepository.findBySessionId(session.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        return ReportResponse.from(report);
    }

    @Transactional
    public ReportResponse generateSessionReport(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        // 리포트는 최종 결과라서 COMPLETED 이후 생성
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (reportRepository.existsBySessionId(session.getId())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        List<Answer> answers = answerRepository.findAllBySessionIdOrderByCreatedAtAsc(session.getId());

        if (answers.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        List<AiReportGenerateRequest.AnswerItem> answerItems = answers.stream()
            .map(answer -> new AiReportGenerateRequest.AnswerItem(
                answer.getId(),
                answer.getQuestionText(),
                answer.getTranscript(),
                answer.getDurationSec(),
                createAudioMetrics(answer),
                createVisionMetrics(answer)
            ))
            .toList();

        List<AiReportGenerateRequest.FeedbackItem> feedbackItems = answers.stream()
            .flatMap(answer -> feedbackRepository.findAllByAnswerIdOrderByCreatedAtAsc(answer.getId()).stream())
            .map(feedback -> new AiReportGenerateRequest.FeedbackItem(
                feedback.getAnswer().getId(),
                feedback.getSummary(),
                feedback.getEvidence(),
                feedback.getImprovementExample(),
                feedback.getStructureScore(),
                feedback.getSpecificityScore(),
                feedback.getRelevanceScore(),
                feedback.getDeliveryScore()
            ))
            .toList();

        AiReportGenerateRequest aiRequest = new AiReportGenerateRequest(
            user.getId(),
            session.getId(),
            session.getMode().name(),
            session.getTarget().name(),
            answerItems,
            feedbackItems
        );

        AiReportGenerateResponse aiResponse = aiGatewayClient.generateReport(aiRequest);

        Report report = Report.create(
            session,
            aiResponse.totalScore(),
            aiResponse.strengths(),
            aiResponse.improvements()
        );

        Report savedReport = reportRepository.save(report);

        return ReportResponse.from(savedReport);
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

        return userRepository.findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE,
                providerUserId
            )
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Map<String, Object> createAudioMetrics(Answer answer) {
        Map<String, Object> audioMetrics = new HashMap<>();
        audioMetrics.put("speech_rate", answer.getSpeechRate());
        audioMetrics.put("silence_count", answer.getSilenceCount());
        audioMetrics.put("filler_word_count", answer.getFillerWordCount());
        return audioMetrics;
    }

    private Map<String, Object> createVisionMetrics(Answer answer) {
        Map<String, Object> visionMetrics = new HashMap<>();
        visionMetrics.put("eye_contact_score", answer.getEyeContactScore());
        visionMetrics.put("posture_score", answer.getPostureScore());
        return visionMetrics;
    }
}
