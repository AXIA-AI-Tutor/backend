package com.ax.avatarcoach.domain.feedback.controller;

import com.ax.avatarcoach.domain.feedback.dto.FeedbackCreateRequest;
import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import com.ax.avatarcoach.domain.feedback.service.FeedbackService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Feedback", description = "피드백 API")
@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 저장", description = "현재 로그인한 사용자의 답변에 피드백 데이터를 저장합니다.")
    @PostMapping
    public ApiResponse<FeedbackResponse> createFeedback(
        @Valid @RequestBody FeedbackCreateRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(feedbackService.createFeedbackForUser(request, oAuth2User));
    }
}
