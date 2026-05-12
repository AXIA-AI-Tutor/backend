package com.ax.avatarcoach.domain.corpus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "corpus.ingest-job")
public record CorpusIngestJobProperties(
    boolean enabled,
    String artifactUri,
    String artifactName,
    String artifactVersion,
    String artifactVariant
) {

    public String artifactName() {
        return artifactName == null || artifactName.isBlank()
            ? "global-corpus"
            : artifactName;
    }

    public String artifactVersion() {
        return artifactVersion == null || artifactVersion.isBlank()
            ? "unknown"
            : artifactVersion;
    }

    public String artifactVariant() {
        return artifactVariant == null || artifactVariant.isBlank()
            ? "demo"
            : artifactVariant;
    }
}
