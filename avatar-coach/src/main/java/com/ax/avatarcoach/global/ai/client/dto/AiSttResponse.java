package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiSttResponse(
    @JsonProperty("transcript")
    String transcript,

    @JsonProperty("chunk_index")
    Integer chunkIndex,

    @JsonProperty("is_final")
    boolean isFinal,

    @JsonProperty("latency_ms")
    int latencyMs,

    @JsonProperty("model_name")
    String modelName,

    @JsonProperty("fallback_components")
    List<String> fallbackComponents
) {
}
