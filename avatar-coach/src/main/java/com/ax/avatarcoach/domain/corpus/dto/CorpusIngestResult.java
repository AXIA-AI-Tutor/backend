package com.ax.avatarcoach.domain.corpus.dto;

public record CorpusIngestResult(
    int sourceChunksSaved,
    int sourceChunksSkipped,
    int recordsSaved,
    int recordsSkipped
) {
}
