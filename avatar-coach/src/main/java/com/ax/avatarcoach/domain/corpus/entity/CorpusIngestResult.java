package com.ax.avatarcoach.domain.corpus.entity;

public record CorpusIngestResult(
    int sourceChunksSaved,
    int sourceChunksSkipped,
    int recordsSaved,
    int recordsSkipped
) {
}
