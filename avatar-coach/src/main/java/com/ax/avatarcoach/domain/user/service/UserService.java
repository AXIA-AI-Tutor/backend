package com.ax.avatarcoach.domain.user.service;

import com.ax.avatarcoach.domain.user.dto.UserMeResponse;
import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.security.oauth.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserMeResponse getMe(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        User user = userRepository.findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE,
                userInfo.getProviderUserId()
            )
            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        return UserMeResponse.from(user);
    }
}
