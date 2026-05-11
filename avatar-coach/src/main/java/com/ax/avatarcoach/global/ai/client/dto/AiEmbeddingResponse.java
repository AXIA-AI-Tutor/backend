package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiEmbeddingResponse(
    @JsonProperty("model_name")
    String modelName,

    @JsonProperty("dimensions")
    int dimensions,

    @JsonProperty("normalized")
    boolean normalized,

    @JsonProperty("items")
    List<Item> items
) {
    public record Item(
        @JsonProperty("id")
        String id,

        @JsonProperty("embedding")
        List<Double> embedding,

        @JsonProperty("latency_ms")
        int latencyMs
    ) {
    }

    public List<Double> firstEmbedding() {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("AI embedding response is empty");
        }
        return items.get(0).embedding();
    }
}
