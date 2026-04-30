package com.ax.avatarcoach.domain.user.controller;

import com.ax.avatarcoach.domain.user.dto.UserMeResponse;
import com.ax.avatarcoach.domain.user.service.UserService;
import com.ax.avatarcoach.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return ApiResponse.success(userService.getMe(oAuth2User));
    }
}
