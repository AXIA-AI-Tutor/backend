package com.ax.avatarcoach.domain.corpus.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GlobalCorpusEmbeddingJdbcRepository {

    private static final int EXPECTED_DIMENSION = 1024;

    private final JdbcTemplate jdbcTemplate;

    public void updateEmbedding(Long recordId, List<Double> embedding) {
        if (embedding == null || embedding.size() != EXPECTED_DIMENSION) {
            throw new IllegalArgumentException("Embedding dimension must be " + EXPECTED_DIMENSION);
        }

        jdbcTemplate.update(
            """
                UPDATE global_corpus_records
                SET embedding = ?::vector
                WHERE id = ?
                """,
            toVectorLiteral(embedding),
            recordId
        );
    }

    private String toVectorLiteral(List<Double> embedding) {
        return embedding.stream()
            .map(value -> String.format(Locale.ROOT, "%.8f", value))
            .collect(Collectors.joining(",", "[", "]"));
    }
}
