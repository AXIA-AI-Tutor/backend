package com.ax.avatarcoach.domain.corpus.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "corpus.ingest-job", name = "enabled", havingValue = "true")
public class GcsCorpusArtifactDownloader {

    private static final String GCS_PREFIX = "gs://";

    private final Storage storage;

    public Path download(String artifactUri) {
        GcsObjectLocation location = parseGcsUri(artifactUri);

        try {
            Path tempFile = Files.createTempFile("corpus-artifact-", ".zip");
            Blob blob = storage.get(BlobId.of(location.bucketName(), location.objectName()));

            if (blob == null || !blob.exists()) {
                throw new IllegalArgumentException("Corpus artifact not found: " + artifactUri);
            }

            blob.downloadTo(tempFile);
            return tempFile;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to download corpus artifact: " + artifactUri, exception);
        }
    }

    private GcsObjectLocation parseGcsUri(String artifactUri) {
        if (artifactUri == null || !artifactUri.startsWith(GCS_PREFIX)) {
            throw new IllegalArgumentException("Corpus artifact uri must start with gs://");
        }

        String path = artifactUri.substring(GCS_PREFIX.length());
        int slashIndex = path.indexOf('/');

        if (slashIndex <= 0 || slashIndex == path.length() - 1) {
            throw new IllegalArgumentException("Invalid corpus artifact uri: " + artifactUri);
        }

        return new GcsObjectLocation(
            path.substring(0, slashIndex),
            path.substring(slashIndex + 1)
        );
    }

    private record GcsObjectLocation(
        String bucketName,
        String objectName
    ) {
    }
}
