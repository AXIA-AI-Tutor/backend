package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

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

    String transcript,

    @JsonProperty("audio_metrics")
    Map<String, Object> audioMetrics,

    @JsonProperty("vision_metrics")
    Map<String, Object> visionMetrics
) {
}
