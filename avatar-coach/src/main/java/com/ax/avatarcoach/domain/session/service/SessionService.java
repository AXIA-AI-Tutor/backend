package com.ax.avatarcoach.domain.session.service;

import com.ax.avatarcoach.domain.session.dto.SessionCreateRequest;
import com.ax.avatarcoach.domain.session.dto.SessionResponse;
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
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SessionResponse createSession(SessionCreateRequest request, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = Session.create(
            user,
            request.mode(),
            request.target(),
            request.difficulty()
        );

        Session savedSession = sessionRepository.save(session);

        return SessionResponse.from(savedSession);
    }

    public SessionResponse getSession(Long sessionId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);

        Session session = sessionRepository.findByIdAndUser(sessionId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        return SessionResponse.from(session);
    }

    private User getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        return userRepository.findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE,
                userInfo.getProviderUserId()
            )
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
    }
}
