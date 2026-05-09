package com.ax.avatarcoach.domain.corpus.controller;

import com.ax.avatarcoach.domain.corpus.dto.CorpusIngestResult;
import com.ax.avatarcoach.domain.corpus.service.GlobalCorpusIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@Profile("local")
@RestController
@RequiredArgsConstructor
public class LocalCorpusIngestController {

    private final GlobalCorpusIngestService ingestService;

    @PostMapping("/api/local/corpus/ingest")
    public CorpusIngestResult ingest(
        @RequestParam String corpusRootPath,
        @RequestParam(defaultValue = "local-corpus") String artifactName,
        @RequestParam(defaultValue = "local") String artifactVersion,
        @RequestParam(defaultValue = "sample") String artifactVariant
    ) {
        Path corpusRootDirectory = Path.of(corpusRootPath);

        return ingestService.ingestAllTargets(
            corpusRootDirectory,
            artifactName,
            artifactVersion,
            artifactVariant,
            corpusRootPath
        );
    }
}
