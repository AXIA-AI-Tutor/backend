package com.ax.avatarcoach.domain.corpus.dto;

import java.util.List;

public record CorpusSearchCondition(
    String query,
    String target,
    List<String> recordTypes,
    String difficulty,
    int limit
) {
    public int safeLimit() {
        if (limit <= 0) {
            return 5;
        }
        return Math.min(limit, 20);
    }
}
