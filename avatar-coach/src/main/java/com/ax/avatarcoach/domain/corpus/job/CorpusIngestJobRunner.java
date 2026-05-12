package com.ax.avatarcoach.domain.corpus.job;

import com.ax.avatarcoach.domain.corpus.config.CorpusIngestJobProperties;
import com.ax.avatarcoach.domain.corpus.dto.CorpusIngestResult;
import com.ax.avatarcoach.domain.corpus.service.CorpusArtifactExtractor;
import com.ax.avatarcoach.domain.corpus.service.GcsCorpusArtifactDownloader;
import com.ax.avatarcoach.domain.corpus.service.GlobalCorpusIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "corpus.ingest-job", name = "enabled", havingValue = "true")
public class CorpusIngestJobRunner implements ApplicationRunner {

    private final CorpusIngestJobProperties properties;
    private final GcsCorpusArtifactDownloader downloader;
    private final CorpusArtifactExtractor extractor;
    private final GlobalCorpusIngestService ingestService;
    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        if (properties.artifactUri() == null || properties.artifactUri().isBlank()) {
            throw new IllegalArgumentException("CORPUS_ARTIFACT_URI is required when corpus ingest job is enabled.");
        }

        log.info("Starting corpus ingest job. artifactUri={}", properties.artifactUri());

        Path artifactZipPath = downloader.download(properties.artifactUri());
        Path extractedDirectory = extractor.extract(artifactZipPath);
        List<Path> corpusRoots = extractor.findCorpusRoots(extractedDirectory);

        CorpusIngestResult totalResult = new CorpusIngestResult(0, 0, 0, 0);

        for (Path corpusRoot : corpusRoots) {
            log.info("Ingesting corpus root. corpusRoot={}", corpusRoot);

            CorpusIngestResult result = ingestService.ingestAllTargets(
                corpusRoot,
                properties.artifactName(),
                properties.artifactVersion(),
                properties.artifactVariant(),
                properties.artifactUri()
            );

            totalResult = add(totalResult, result);
        }

        log.info(
            "Completed corpus ingest job. sourceChunksSaved={}, sourceChunksSkipped={}, recordsSaved={}, recordsSkipped={}",
            totalResult.sourceChunksSaved(),
            totalResult.sourceChunksSkipped(),
            totalResult.recordsSaved(),
            totalResult.recordsSkipped()
        );

        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }

    private CorpusIngestResult add(CorpusIngestResult left, CorpusIngestResult right) {
        return new CorpusIngestResult(
            left.sourceChunksSaved() + right.sourceChunksSaved(),
            left.sourceChunksSkipped() + right.sourceChunksSkipped(),
            left.recordsSaved() + right.recordsSaved(),
            left.recordsSkipped() + right.recordsSkipped()
        );
    }
}
