package com.ax.avatarcoach.domain.corpus.dto;

public record CorpusSearchResult(
    Long id,
    String recordId,
    String target,
    String recordType,
    String difficulty,
    String followupStrategy,
    String topicPathJson,
    String tagsJson,
    String embeddingTextKo,
    String questionKo,
    String answerKo,
    String followupQuestionKo,
    String conceptKo,
    String interviewUse,
    String rubricJson,
    String followupPatternExtJson,
    String sourceRefsJson,
    double distance,
    double score
) {
}
