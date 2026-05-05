package com.ax.avatarcoach.domain.session.controller;

import com.ax.avatarcoach.domain.session.dto.SessionCreateRequest;
import com.ax.avatarcoach.domain.session.dto.SessionEventResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Session", description = "연습 세션 API")
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(
        summary = "세션 생성",
        description = "현재 로그인한 사용자의 연습 세션을 생성합니다. 생성된 세션은 READY 상태로 시작합니다."
    )
    @PostMapping
    public ApiResponse<SessionResponse> createSession(
        @Valid @RequestBody SessionCreateRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.createSession(request, oAuth2User));
    }

    @Operation(
        summary = "내 세션 목록 조회",
        description = "현재 로그인한 사용자의 세션 목록을 최신순으로 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<SessionResponse>> getMySessions(
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.getMySessions(oAuth2User));
    }

    @Operation(
        summary = "세션 단건 조회",
        description = "현재 로그인한 사용자의 특정 세션 정보를 조회합니다."
    )
    @GetMapping("/{sessionId}")
    public ApiResponse<SessionResponse> getSession(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.getSession(sessionId, oAuth2User));
    }

    @Operation(
        summary = "세션 시작",
        description = "READY 상태의 세션을 IN_PROGRESS 상태로 변경하고 시작 시각을 기록합니다."
    )
    @PatchMapping("/{sessionId}/start")
    public ApiResponse<SessionResponse> startSession(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.startSession(sessionId, oAuth2User));
    }

    @Operation(
        summary = "세션 종료",
        description = "IN_PROGRESS 상태의 세션을 COMPLETED 상태로 변경하고 종료 시각을 기록합니다."
    )
    @PatchMapping("/{sessionId}/complete")
    public ApiResponse<SessionResponse> completeSession(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.completeSession(sessionId, oAuth2User));
    }

    @Operation(
        summary = "세션 이벤트 목록 조회",
        description = "현재 로그인한 사용자의 특정 세션에 기록된 이벤트 목록을 순서대로 조회합니다."
    )
    @GetMapping("/{sessionId}/events")
    public ApiResponse<List<SessionEventResponse>> getSessionEvents(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.getSessionEvents(sessionId, oAuth2User));
    }
}
