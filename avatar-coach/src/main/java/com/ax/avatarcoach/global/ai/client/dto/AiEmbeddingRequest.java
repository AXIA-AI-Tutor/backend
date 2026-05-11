package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiEmbeddingRequest(
    @JsonProperty("purpose")
    String purpose,

    @JsonProperty("inputs")
    List<Input> inputs,

    @JsonProperty("normalize")
    boolean normalize
) {
    public static AiEmbeddingRequest query(String id, String text) {
        return new AiEmbeddingRequest(
            "query",
            List.of(new Input(id, text)),
            true
        );
    }

    public static AiEmbeddingRequest ingest(String id, String text) {
        return new AiEmbeddingRequest(
            "ingest",
            List.of(new Input(id, text)),
            true
        );
    }

    public record Input(
        @JsonProperty("id")
        String id,

        @JsonProperty("text")
        String text
    ) {
    }
}
