package com.ax.avatarcoach.domain.answer.controller;

import com.ax.avatarcoach.domain.answer.dto.AnswerResponse;
import com.ax.avatarcoach.domain.answer.dto.InternalAnswerCreateRequest;
import com.ax.avatarcoach.domain.answer.service.AnswerService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal Answer", description = "내부 답변 저장 API")
@RestController
@RequestMapping("/internal/sessions/{sessionId}/answers")
@RequiredArgsConstructor
public class InternalAnswerController {

    private final AnswerService answerService;

    @Operation(summary = "내부 답변 저장", description = "AI API 서버가 세션에 답변 데이터를 저장합니다.")
    @PostMapping
    public ApiResponse<AnswerResponse> createAnswer(
        @PathVariable Long sessionId,
        @Valid @RequestBody InternalAnswerCreateRequest request
    ) {
        return ApiResponse.success(answerService.createAnswerInternal(sessionId, request));
    }
}
