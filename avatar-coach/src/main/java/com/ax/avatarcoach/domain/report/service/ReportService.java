package com.ax.avatarcoach.domain.report.service;

import com.ax.avatarcoach.domain.report.dto.ReportResponse;
import com.ax.avatarcoach.domain.report.entity.Report;
import com.ax.avatarcoach.domain.report.repository.ReportRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public ReportResponse getSessionReport(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        Report report = reportRepository.findBySessionId(session.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        return ReportResponse.from(report);
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
}
