package com.ax.avatarcoach.domain.corpus.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CorpusSourceChunkLine(
    String sourceChunkId,
    String target,
    String sourceId,
    String sourceTitle,
    String sourceType,
    String sourceUrl,
    String sourceVersion,
    String sourceLicense,
    Integer chunkIndex,
    List<String> headingPath,
    String text,
    String textHash,
    String sourceHash,
    String rawTextHash,
    String normalizedTextHash,
    JsonNode metadata,
    JsonNode sourceRefs
) {
}
