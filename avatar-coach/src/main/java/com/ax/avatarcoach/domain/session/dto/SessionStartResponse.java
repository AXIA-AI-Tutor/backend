package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세션 시작 응답")
public record SessionStartResponse(
    @Schema(description = "시작된 세션 정보")
    SessionResponse session,

    @Schema(description = "AI가 생성한 첫 번째 질문")
    AiQuestionGenerateResponse question
) {
    public static SessionStartResponse of(
        SessionResponse session,
        AiQuestionGenerateResponse question
    ) {
        return new SessionStartResponse(session, question);
    }
}
