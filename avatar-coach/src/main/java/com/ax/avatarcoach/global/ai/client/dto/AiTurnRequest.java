package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiTurnRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("session_id")
    Long sessionId,

    @JsonProperty("answer_id")
    Long answerId,

    String mode,

    @JsonProperty("question_text")
    String questionText,

    @JsonProperty("vision_metrics")
    String visionMetrics
) {
}
