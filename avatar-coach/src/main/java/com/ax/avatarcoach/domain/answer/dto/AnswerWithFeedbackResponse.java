package com.ax.avatarcoach.domain.answer.dto;

import com.ax.avatarcoach.domain.feedback.dto.FeedbackResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "답변 제출 및 피드백 생성 응답")
public record AnswerWithFeedbackResponse(
    @Schema(description = "저장된 답변 정보")
    AnswerResponse answer,

    @Schema(description = "AI가 생성한 피드백 정보")
    FeedbackResponse feedback
) {
    public static AnswerWithFeedbackResponse of(
        AnswerResponse answer,
        FeedbackResponse feedback
    ) {
        return new AnswerWithFeedbackResponse(answer, feedback);
    }
}
