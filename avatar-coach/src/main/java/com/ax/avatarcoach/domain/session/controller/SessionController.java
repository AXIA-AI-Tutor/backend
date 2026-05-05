package com.ax.avatarcoach.domain.session.controller;

import com.ax.avatarcoach.domain.session.dto.SessionCreateRequest;
import com.ax.avatarcoach.domain.session.dto.SessionResponse;
import com.ax.avatarcoach.domain.session.service.SessionService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Session", description = "연습 세션 API")
@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "세션 생성", description = "현재 로그인한 사용자의 연습 세션을 생성합니다.")
    @PostMapping("/api/sessions")
    public ApiResponse<SessionResponse> createSession(
        @Valid @RequestBody SessionCreateRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.createSession(request, oAuth2User));
    }

    @Operation(
        summary = "세션 단건 조회",
        description = "현재 로그인한 사용자의 세션 정보를 조회합니다."
    )
    @GetMapping("/api/sessions/{sessionId}")
    public ApiResponse<SessionResponse> getSession(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.getSession(sessionId, oAuth2User));
    }

    @Operation(summary = "세션 시작", description = "READY 상태의 세션을 IN_PROGRESS 상태로 변경합니다.")
    @PatchMapping("/api/sessions/{sessionId}/start")
    public ApiResponse<SessionResponse> startSession(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.startSession(sessionId, oAuth2User));
    }

    @Operation(summary = "세션 종료", description = "IN_PROGRESS 상태의 세션을 COMPLETED 상태로 변경합니다.")
    @PatchMapping("/api/sessions/{sessionId}/complete")
    public ApiResponse<SessionResponse> completeSession(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.completeSession(sessionId, oAuth2User));
    }
}
