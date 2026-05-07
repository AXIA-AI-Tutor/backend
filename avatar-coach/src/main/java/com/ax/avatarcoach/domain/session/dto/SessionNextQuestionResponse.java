package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "다음 질문 생성 응답")
public record SessionNextQuestionResponse(
    @Schema(description = "세션 ID", example = "1")
    Long sessionId,

    @Schema(description = "이번에 생성된 질문 번호", example = "2")
    Integer questionIndex,

    @Schema(description = "최대 질문 수", example = "4")
    Integer maxQuestionCount,

    @Schema(description = "질문 유형", example = "FOLLOW_UP")
    String questionType,

    @Schema(description = "AI가 생성한 질문")
    AiQuestionGenerateResponse question
) {
    public static SessionNextQuestionResponse of(
        Long sessionId,
        Integer questionIndex,
        Integer maxQuestionCount,
        AiQuestionGenerateResponse question
    ) {
        return new SessionNextQuestionResponse(
            sessionId,
            questionIndex,
            maxQuestionCount,
            questionTypeOf(questionIndex),
            question
        );
    }

    private static String questionTypeOf(Integer questionIndex) {
        if (questionIndex == 2 || questionIndex == 4) {
            return "FOLLOW_UP";
        }
        return "BASIC";
    }
}
