package com.ax.avatarcoach.domain.corpus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag.embedding")
public record CorpusEmbeddingProperties(
    String provider,
    String baseUrl,
    String model,
    int dimensions,
    boolean normalize,
    int queryTimeoutMs,
    int ingestTimeoutMs,
    int maxRetries
) {
    public String provider() {
        return provider == null || provider.isBlank() ? "fake" : provider;
    }

    public String baseUrl() {
        return baseUrl == null || baseUrl.isBlank() ? "http://localhost:11434" : baseUrl;
    }

    public String model() {
        return model == null || model.isBlank() ? "bge-m3:latest" : model;
    }

    public int dimensions() {
        return dimensions <= 0 ? 1024 : dimensions;
    }

    public int queryTimeoutMs() {
        return queryTimeoutMs <= 0 ? 5000 : queryTimeoutMs;
    }

    public int ingestTimeoutMs() {
        return ingestTimeoutMs <= 0 ? 60000 : ingestTimeoutMs;
    }

    public int maxRetries() {
        return Math.max(maxRetries, 0);
    }
}
