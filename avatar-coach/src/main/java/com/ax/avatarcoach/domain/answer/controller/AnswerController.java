package com.ax.avatarcoach.domain.answer.controller;

import com.ax.avatarcoach.domain.answer.dto.AnswerCreateRequest;
import com.ax.avatarcoach.domain.answer.dto.AnswerResponse;
import com.ax.avatarcoach.domain.answer.service.AnswerService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Answer", description = "답변 API")
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "답변 저장", description = "현재 로그인한 사용자의 세션에 답변 데이터를 저장합니다.")
    @PostMapping
    public ApiResponse<AnswerResponse> createAnswer(
        @Valid @RequestBody AnswerCreateRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(answerService.createAnswer(request, oAuth2User));
    }
}
