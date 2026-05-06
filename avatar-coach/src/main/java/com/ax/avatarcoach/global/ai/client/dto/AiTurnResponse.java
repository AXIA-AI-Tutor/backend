package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiTurnResponse(
    String summary,

    String evidence,

    @JsonProperty("improvement_example")
    String improvementExample,

    @JsonProperty("structure_score")
    Integer structureScore,

    @JsonProperty("specificity_score")
    Integer specificityScore,

    @JsonProperty("relevance_score")
    Integer relevanceScore,

    @JsonProperty("delivery_score")
    Integer deliveryScore,

    @JsonProperty("tts_audio_url")
    String ttsAudioUrl,

    @JsonProperty("latency_ms")
    Integer latencyMs,

    @JsonProperty("fallback_components")
    List<String> fallbackComponents
) {
}
