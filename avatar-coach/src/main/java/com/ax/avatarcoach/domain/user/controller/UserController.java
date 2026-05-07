package com.ax.avatarcoach.domain.user.controller;

import com.ax.avatarcoach.domain.user.dto.UserMeResponse;
import com.ax.avatarcoach.domain.user.service.UserService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "현재 로그인 사용자 조회",
        description = "Google OAuth2 로그인 후 세션에 저장된 현재 사용자 정보를 조회합니다."
    )
    @GetMapping("/api/users/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return ApiResponse.success(userService.getMe(oAuth2User));
    }
}
