package com.ax.avatarcoach.domain.feedback.controller;

import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import com.ax.avatarcoach.domain.feedback.dto.InternalFeedbackCreateRequest;
import com.ax.avatarcoach.domain.feedback.service.FeedbackService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal Feedback", description = "내부 피드백 저장 API")
@RestController
@RequestMapping("/internal/answers/{answerId}/feedbacks")
@RequiredArgsConstructor
public class InternalFeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "내부 피드백 저장", description = "AI API 서버가 답변에 피드백 데이터를 저장합니다.")
    @PostMapping
    public ApiResponse<FeedbackResponse> createFeedback(
        @PathVariable Long answerId,
        @Valid @RequestBody InternalFeedbackCreateRequest request
    ) {
        return ApiResponse.success(feedbackService.createFeedbackInternal(answerId, request));
    }
}
