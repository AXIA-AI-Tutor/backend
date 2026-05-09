package com.ax.avatarcoach.domain.corpus.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CorpusRecordLine(
    String id,
    String requestId,
    String target,
    String language,
    String recordType,
    String difficulty,
    List<String> topicPath,
    List<String> tags,
    String followupStrategy,
    String embeddingTextKo,
    String questionKo,
    String answerKo,
    String followupQuestionKo,
    String conceptKo,
    String interviewUse,
    JsonNode rubric,
    JsonNode followupPatternExt,
    JsonNode sourceRefs,
    JsonNode quality,
    JsonNode createdBy
) {
}
