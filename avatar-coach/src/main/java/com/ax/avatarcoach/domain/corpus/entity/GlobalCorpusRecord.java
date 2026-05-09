package com.ax.avatarcoach.domain.corpus.entity;

import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(
    name = "global_corpus_records",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_global_corpus_records_record_id", columnNames = "record_id")
    },
    indexes = {
        @Index(name = "idx_global_corpus_records_filter", columnList = "target, language, record_type, difficulty"),
        @Index(name = "idx_global_corpus_records_source_id", columnList = "source_id"),
        @Index(name = "idx_global_corpus_records_followup_strategy", columnList = "followup_strategy")
    }
)
public class GlobalCorpusRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private GlobalCorpusSource source;

    @Column(name = "record_id", nullable = false, length = 150)
    private String recordId;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "target", nullable = false, length = 30)
    private String target;

    @Column(name = "language", nullable = false, length = 10)
    private String language;

    @Column(name = "record_type", nullable = false, length = 50)
    private String recordType;

    @Column(name = "difficulty", length = 30)
    private String difficulty;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "topic_path", nullable = false, columnDefinition = "jsonb")
    private String topicPathJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", nullable = false, columnDefinition = "jsonb")
    private String tagsJson = "[]";

    @Column(name = "followup_strategy", length = 50)
    private String followupStrategy;

    @Column(name = "embedding_text_ko", nullable = false, columnDefinition = "text")
    private String embeddingTextKo;

    @Column(name = "question_ko", columnDefinition = "text")
    private String questionKo;

    @Column(name = "answer_ko", columnDefinition = "text")
    private String answerKo;

    @Column(name = "followup_question_ko", columnDefinition = "text")
    private String followupQuestionKo;

    @Column(name = "concept_ko", columnDefinition = "text")
    private String conceptKo;

    @Column(name = "interview_use", columnDefinition = "text")
    private String interviewUse;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rubric", nullable = false, columnDefinition = "jsonb")
    private String rubricJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "followup_pattern_ext", nullable = false, columnDefinition = "jsonb")
    private String followupPatternExtJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_refs", nullable = false, columnDefinition = "jsonb")
    private String sourceRefsJson = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quality", nullable = false, columnDefinition = "jsonb")
    private String qualityJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "created_by", nullable = false, columnDefinition = "jsonb")
    private String createdByJson = "{}";

    public static GlobalCorpusRecord create(
        GlobalCorpusSource source,
        String recordId,
        String requestId,
        String target,
        String language,
        String recordType,
        String difficulty,
        String topicPathJson,
        String tagsJson,
        String followupStrategy,
        String embeddingTextKo,
        String questionKo,
        String answerKo,
        String followupQuestionKo,
        String conceptKo,
        String interviewUse,
        String rubricJson,
        String followupPatternExtJson,
        String sourceRefsJson,
        String qualityJson,
        String createdByJson
    ) {
        GlobalCorpusRecord record = new GlobalCorpusRecord();
        record.source = source;
        record.recordId = recordId;
        record.requestId = requestId;
        record.target = target;
        record.language = language == null || language.isBlank() ? "ko" : language;
        record.recordType = recordType;
        record.difficulty = difficulty;
        record.topicPathJson = defaultJsonArray(topicPathJson);
        record.tagsJson = defaultJsonArray(tagsJson);
        record.followupStrategy = followupStrategy;
        record.embeddingTextKo = embeddingTextKo;
        record.questionKo = questionKo;
        record.answerKo = answerKo;
        record.followupQuestionKo = followupQuestionKo;
        record.conceptKo = conceptKo;
        record.interviewUse = interviewUse;
        record.rubricJson = defaultJsonObject(rubricJson);
        record.followupPatternExtJson = defaultJsonObject(followupPatternExtJson);
        record.sourceRefsJson = defaultJsonArray(sourceRefsJson);
        record.qualityJson = defaultJsonObject(qualityJson);
        record.createdByJson = defaultJsonObject(createdByJson);
        return record;
    }

    private static String defaultJsonArray(String value) {
        return value == null || value.isBlank() ? "[]" : value;
    }

    private static String defaultJsonObject(String value) {
        return value == null || value.isBlank() ? "{}" : value;
    }
}
