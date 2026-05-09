package com.ax.avatarcoach.domain.corpus.entity;

import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Table(
    name = "global_corpus_sources",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_global_corpus_sources_source_key", columnNames = "source_key")
    }
)
public class GlobalCorpusSource extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_key", nullable = false, length = 150)
    private String sourceKey;

    @Column(name = "source_title", length = 255)
    private String sourceTitle;

    @Column(name = "source_license", length = 100)
    private String sourceLicense;

    @Column(name = "artifact_name", length = 255)
    private String artifactName;

    @Column(name = "artifact_version", length = 100)
    private String artifactVersion;

    @Column(name = "artifact_variant", length = 100)
    private String artifactVariant;

    @Column(name = "artifact_uri", length = 1000)
    private String artifactUri;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", nullable = false, columnDefinition = "jsonb")
    private String metadataJson = "{}";

    public static GlobalCorpusSource create(
        String sourceKey,
        String sourceTitle,
        String sourceLicense,
        String artifactName,
        String artifactVersion,
        String artifactVariant,
        String artifactUri,
        String metadataJson
    ) {
        GlobalCorpusSource source = new GlobalCorpusSource();
        source.sourceKey = sourceKey;
        source.sourceTitle = sourceTitle;
        source.sourceLicense = sourceLicense;
        source.artifactName = artifactName;
        source.artifactVersion = artifactVersion;
        source.artifactVariant = artifactVariant;
        source.artifactUri = artifactUri;
        source.metadataJson = metadataJson == null || metadataJson.isBlank() ? "{}" : metadataJson;
        return source;
    }
}
