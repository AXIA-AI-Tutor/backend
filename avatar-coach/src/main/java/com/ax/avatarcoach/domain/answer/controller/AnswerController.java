package com.ax.avatarcoach.domain.answer.controller;

import com.ax.avatarcoach.domain.answer.dto.AnswerCreateRequest;
import com.ax.avatarcoach.domain.answer.dto.AnswerResponse;
import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import com.ax.avatarcoach.domain.answer.service.AnswerService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Answer", description = "답변 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "답변 저장", description = "현재 로그인한 사용자의 세션에 답변 데이터를 저장합니다.")
    @PostMapping("/answers")
    public ApiResponse<AnswerResponse> createAnswer(
        @Valid @RequestBody AnswerCreateRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(answerService.createAnswer(request, oAuth2User));
    }

    @Operation(summary = "세션별 답변 목록 조회", description = "현재 로그인한 사용자의 세션에 속한 답변 목록을 생성일(createdAt) 오름차순으로 조회합니다.")
    @GetMapping("/sessions/{sessionId}/answers")
    public ApiResponse<List<AnswerResponse>> getSessionAnswers(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(answerService.getSessionAnswers(sessionId, oAuth2User));
    }

    @Operation(summary = "답변 단건 조회", description = "현재 로그인한 사용자의 세션에 속한 답변 단건을 조회합니다.")
    @GetMapping("/answers/{answerId}")
    public ApiResponse<AnswerResponse> getAnswer(
        @PathVariable Long answerId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(answerService.getAnswer(answerId, oAuth2User));
    }

    @Operation(summary = "답변별 피드백 목록 조회", description = "현재 로그인한 사용자의 답변에 속한 피드백 목록을 생성일(createdAt) 오름차순으로 조회합니다.")
    @GetMapping("/answers/{answerId}/feedbacks")
    public ApiResponse<List<FeedbackResponse>> getAnswerFeedbacks(
        @PathVariable Long answerId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(answerService.getAnswerFeedbacks(answerId, oAuth2User));
    }
}
