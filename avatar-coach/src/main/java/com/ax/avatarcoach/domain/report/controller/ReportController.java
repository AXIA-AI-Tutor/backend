package com.ax.avatarcoach.domain.report.controller;

import com.ax.avatarcoach.domain.report.dto.ReportResponse;
import com.ax.avatarcoach.domain.report.service.ReportService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report", description = "리포트 API")
@RestController
@RequestMapping("/api/sessions/{sessionId}/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(
        summary = "세션별 리포트 조회",
        description = "현재 로그인한 사용자의 특정 세션에 생성된 리포트를 조회합니다."
    )
    @GetMapping
    public ApiResponse<ReportResponse> getSessionReport(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(reportService.getSessionReport(sessionId, oAuth2User));
    }

    @Operation(
        summary = "AI 리포트 생성",
        description = "완료된 세션의 답변과 피드백을 기반으로 AI 서버에 리포트 생성을 요청하고, 생성된 리포트를 저장합니다."
    )
    @PostMapping("/generate")
    public ApiResponse<ReportResponse> generateSessionReport(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(reportService.generateSessionReport(sessionId, oAuth2User));
    }
}
