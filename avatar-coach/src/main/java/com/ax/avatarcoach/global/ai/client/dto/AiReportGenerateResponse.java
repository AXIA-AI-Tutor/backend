package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiReportGenerateResponse(
    @JsonProperty("total_score")
    Integer totalScore,

    String strengths,

    String improvements,

    @JsonProperty("latency_ms")
    Integer latencyMs,

    @JsonProperty("fallback_components")
    List<String> fallbackComponents
) {
}
