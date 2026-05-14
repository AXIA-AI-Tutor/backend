package com.ax.avatarcoach.domain.session.controller;

import com.ax.avatarcoach.domain.session.dto.*;
import com.ax.avatarcoach.domain.session.service.SessionService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Session", description = "연습 세션 API")
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(
        summary = "세션 생성",
        description = "현재 로그인한 사용자의 연습 준비 세션을 생성합니다. 생성된 세션은 READY 상태이며, 문서 업로드와 옵션 선택에 사용할 sessionId를 반환합니다."
    )
    @PostMapping
    public ApiResponse<SessionResponse> createSession(
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.createSession(oAuth2User));
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
        description = "READY 상태의 세션에 최종 옵션을 저장하고 IN_PROGRESS 상태로 변경합니다. \n" +
            "업로드 완료(UPLOADED) 문서가 1개 이상 필요하며, READY_FOR_AI 또는 COMPLETED 상태를 인정합니다. \n" +
            "요약(summary) non-blank 문서가 없으면 DOCUMENT_SUMMARY_NOT_READY를 반환합니다. \n" +
            "MVP-2 멀티턴 흐름에서 1번 질문은 이 API 응답으로 반환합니다. \n" +
            "2~4번 질문은 답변 제출 후 POST /api/sessions/{sessionId}/questions/next API로 생성합니다. \n" +
            "AI 서버 연결 실패 시 AI_SERVER_UNAVAILABLE, AI 서버 응답 오류 시 AI_SERVER_ERROR를 반환합니다."
    )
    @PatchMapping("/{sessionId}/start")
    public ApiResponse<SessionStartResponse> startSession(
        @PathVariable Long sessionId,
        @Valid @RequestBody SessionStartRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.startSession(sessionId, request, oAuth2User));
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

    @Operation(
        summary = "다음 질문 생성",
        description = "MVP-2 멀티턴 흐름에서 2~4번 질문을 생성합니다. \n" +
            "1번 질문은 세션 시작 API에서 반환되므로 이 API로 생성하지 않습니다. \n" +
            "Backend가 현재 저장된 답변 개수를 기준으로 다음 questionIndex를 계산합니다. \n" +
            "questionIndex 2와 4는 이전 답변 기반 FOLLOW_UP 질문이고, questionIndex 3은 새로운 BASIC 질문입니다. \n" +
            "답변이 4개 이상 저장된 세션은 다음 질문을 생성할 수 없습니다. \n" +
            "AI 서버 연결 실패 시 AI_SERVER_UNAVAILABLE, AI 서버 응답 오류 시 AI_SERVER_ERROR를 반환합니다."
    )
    @PostMapping("/{sessionId}/questions/next")
    public ApiResponse<SessionNextQuestionResponse> generateNextQuestion(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(sessionService.generateNextQuestion(sessionId, oAuth2User));
    }
}
