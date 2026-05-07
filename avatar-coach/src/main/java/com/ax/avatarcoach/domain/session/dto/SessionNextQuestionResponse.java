package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "다음 질문 생성 응답")
public record SessionNextQuestionResponse(
    @Schema(description = "세션 ID", example = "1")
    Long sessionId,

    @Schema(description = "이번에 생성된 질문 번호입니다. 2~4 범위의 값만 반환됩니다.", example = "2")
    Integer questionIndex,

    @Schema(description = "MVP-2 세션의 최대 질문 수입니다.", example = "4")
    Integer maxQuestionCount,

    @Schema(description = "질문 유형입니다. BASIC은 새로운 기본 질문, FOLLOW_UP은 이전 답변 기반 꼬리질문입니다.", example = "FOLLOW_UP")
    String questionType,

    @Schema(description = "AI가 생성한 다음 질문입니다. FE는 question_text와 tts_audio_url을 사용자에게 표시할 수 있습니다.")
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
