package com.ax.avatarcoach.domain.corpus.entity;

import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(
    name = "global_corpus_source_chunks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_global_corpus_source_chunks_chunk_id", columnNames = "source_chunk_id")
    },
    indexes = {
        @Index(name = "idx_global_corpus_source_chunks_source_id", columnList = "source_id"),
        @Index(name = "idx_global_corpus_source_chunks_target_source_key", columnList = "target, source_key")
    }
)
public class GlobalCorpusSourceChunk extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private GlobalCorpusSource source;

    @Column(name = "source_chunk_id", nullable = false, length = 200)
    private String sourceChunkId;

    @Column(name = "target", nullable = false, length = 30)
    private String target;

    @Column(name = "source_key", nullable = false, length = 150)
    private String sourceKey;

    @Column(name = "source_title", length = 255)
    private String sourceTitle;

    @Column(name = "source_type", length = 100)
    private String sourceType;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(name = "source_version", length = 255)
    private String sourceVersion;

    @Column(name = "source_license", length = 100)
    private String sourceLicense;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "heading_path", nullable = false, columnDefinition = "jsonb")
    private String headingPathJson = "[]";

    @Column(name = "text", nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "text_hash", length = 100)
    private String textHash;

    @Column(name = "source_hash", length = 100)
    private String sourceHash;

    @Column(name = "raw_text_hash", length = 100)
    private String rawTextHash;

    @Column(name = "normalized_text_hash", length = 100)
    private String normalizedTextHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
    private String metadataJson = "{}";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_refs", nullable = false, columnDefinition = "jsonb")
    private String sourceRefsJson = "[]";

    public static GlobalCorpusSourceChunk create(
        GlobalCorpusSource source,
        String sourceChunkId,
        String target,
        String sourceKey,
        String sourceTitle,
        String sourceType,
        String sourceUrl,
        String sourceVersion,
        String sourceLicense,
        Integer chunkIndex,
        String headingPathJson,
        String text,
        String textHash,
        String sourceHash,
        String rawTextHash,
        String normalizedTextHash,
        String metadataJson,
        String sourceRefsJson
    ) {
        GlobalCorpusSourceChunk chunk = new GlobalCorpusSourceChunk();
        chunk.source = source;
        chunk.sourceChunkId = sourceChunkId;
        chunk.target = target;
        chunk.sourceKey = sourceKey;
        chunk.sourceTitle = sourceTitle;
        chunk.sourceType = sourceType;
        chunk.sourceUrl = sourceUrl;
        chunk.sourceVersion = sourceVersion;
        chunk.sourceLicense = sourceLicense;
        chunk.chunkIndex = chunkIndex;
        chunk.headingPathJson = defaultJsonArray(headingPathJson);
        chunk.text = text;
        chunk.textHash = textHash;
        chunk.sourceHash = sourceHash;
        chunk.rawTextHash = rawTextHash;
        chunk.normalizedTextHash = normalizedTextHash;
        chunk.metadataJson = defaultJsonObject(metadataJson);
        chunk.sourceRefsJson = defaultJsonArray(sourceRefsJson);
        return chunk;
    }

    private static String defaultJsonArray(String value) {
        return value == null || value.isBlank() ? "[]" : value;
    }

    private static String defaultJsonObject(String value) {
        return value == null || value.isBlank() ? "{}" : value;
    }
}
