package com.ax.avatarcoach.global.security.oauth;

import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String INVALID_USER_INFO_ERROR_CODE = "invalid_user_info";

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        String providerUserId = requireAttribute("sub", userInfo.getProviderUserId());
        String email = requireAttribute("email", userInfo.getEmail());

        userRepository.findByProviderAndProviderUserId(
                OAuthProvider.GOOGLE,
                providerUserId
            )
            .orElseGet(() -> userRepository.save(
                User.createOAuthUser(
                    email,
                    userInfo.getNickname(),
                    userInfo.getProfileImageUrl(),
                    OAuthProvider.GOOGLE,
                    providerUserId
                )
            ));

        return oAuth2User;
    }

    private String requireAttribute(String attributeName, String value) {
        if (value == null || value.isBlank()) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(INVALID_USER_INFO_ERROR_CODE),
                "Required OAuth attribute is missing: " + attributeName
            );
        }
        return value;
    }
}
