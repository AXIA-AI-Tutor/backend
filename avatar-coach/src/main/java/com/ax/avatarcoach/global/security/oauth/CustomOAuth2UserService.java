package com.ax.avatarcoach.global.security.oauth;

import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        userRepository.findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE,
                userInfo.getProviderUserId()
            )
            .orElseGet(() -> userRepository.save(
                User.createOAuthUser(
                    userInfo.getEmail(),
                    userInfo.getNickname(),
                    userInfo.getProfileImageUrl(),
                    OAuthProvider.GOOGLE,
                    userInfo.getProviderUserId()
                )
            ));

        return oAuth2User;
    }
}
