package com.ax.avatarcoach.domain.corpus.client;

import java.util.List;

public record OllamaEmbedResponse(
    String model,
    List<List<Double>> embeddings
) {
    public List<Double> firstEmbedding() {
        if (embeddings == null || embeddings.isEmpty()) {
            return List.of();
        }
        return embeddings.get(0);
    }
}
