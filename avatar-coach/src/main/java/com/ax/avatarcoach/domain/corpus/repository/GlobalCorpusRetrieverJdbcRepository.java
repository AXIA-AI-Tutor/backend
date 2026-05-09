package com.ax.avatarcoach.domain.corpus.repository;

import com.ax.avatarcoach.domain.corpus.entity.CorpusSearchCondition;
import com.ax.avatarcoach.domain.corpus.entity.CorpusSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GlobalCorpusRetrieverJdbcRepository {

    private static final int EXPECTED_DIMENSION = 1024;

    private final JdbcTemplate jdbcTemplate;

    public List<CorpusSearchResult> search(CorpusSearchCondition condition, List<Double> queryEmbedding) {
        if (queryEmbedding == null || queryEmbedding.size() != EXPECTED_DIMENSION) {
            throw new IllegalArgumentException("Query embedding dimension must be " + EXPECTED_DIMENSION);
        }

        StringBuilder sql = new StringBuilder(
            """
                SELECT
                    id,
                    record_id,
                    target,
                    record_type,
                    difficulty,
                    followup_strategy,
                    topic_path::text AS topic_path_json,
                    tags::text AS tags_json,
                    embedding_text_ko,
                    question_ko,
                    answer_ko,
                    followup_question_ko,
                    concept_ko,
                    interview_use,
                    rubric::text AS rubric_json,
                    followup_pattern_ext::text AS followup_pattern_ext_json,
                    source_refs::text AS source_refs_json,
                    embedding <=> ?::vector AS distance,
                    1 - (embedding <=> ?::vector) AS score
                FROM global_corpus_records
                WHERE embedding IS NOT NULL
                """
        );

        List<Object> params = new ArrayList<>();
        String vectorLiteral = toVectorLiteral(queryEmbedding);
        params.add(vectorLiteral);
        params.add(vectorLiteral);

        if (condition.target() != null && !condition.target().isBlank()) {
            sql.append(" AND target = ?");
            params.add(condition.target());
        }

        if (!CollectionUtils.isEmpty(condition.recordTypes())) {
            String placeholders = condition.recordTypes().stream()
                .map(ignored -> "?")
                .collect(Collectors.joining(", "));

            sql.append(" AND record_type IN (").append(placeholders).append(")");
            params.addAll(condition.recordTypes());
        }

        if (condition.difficulty() != null && !condition.difficulty().isBlank()) {
            sql.append(" AND difficulty = ?");
            params.add(condition.difficulty());
        }

        sql.append(" ORDER BY embedding <=> ?::vector");
        params.add(vectorLiteral);

        sql.append(" LIMIT ?");
        params.add(condition.safeLimit());

        return jdbcTemplate.query(
            sql.toString(),
            (rs, rowNum) -> new CorpusSearchResult(
                rs.getLong("id"),
                rs.getString("record_id"),
                rs.getString("target"),
                rs.getString("record_type"),
                rs.getString("difficulty"),
                rs.getString("followup_strategy"),
                rs.getString("topic_path_json"),
                rs.getString("tags_json"),
                rs.getString("embedding_text_ko"),
                rs.getString("question_ko"),
                rs.getString("answer_ko"),
                rs.getString("followup_question_ko"),
                rs.getString("concept_ko"),
                rs.getString("interview_use"),
                rs.getString("rubric_json"),
                rs.getString("followup_pattern_ext_json"),
                rs.getString("source_refs_json"),
                rs.getDouble("distance"),
                rs.getDouble("score")
            ),
            params.toArray()
        );
    }

    private String toVectorLiteral(List<Double> embedding) {
        return embedding.stream()
            .map(value -> String.format(Locale.ROOT, "%.8f", value))
            .collect(Collectors.joining(",", "[", "]"));
    }
}
