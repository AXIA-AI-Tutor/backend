package com.ax.avatarcoach.domain.corpus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "corpus.embedding-job")
public record CorpusEmbeddingJobProperties(
    boolean enabled,
    int batchSize,
    int maxBatches
) {

    public int batchSize() {
        return batchSize <= 0 ? 100 : batchSize;
    }

    public int maxBatches() {
        return maxBatches <= 0 ? 100 : maxBatches;
    }
}
