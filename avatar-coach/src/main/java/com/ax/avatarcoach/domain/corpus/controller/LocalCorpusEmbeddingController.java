package com.ax.avatarcoach.domain.corpus.controller;

import com.ax.avatarcoach.domain.corpus.service.GlobalCorpusEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequiredArgsConstructor
public class LocalCorpusEmbeddingController {

    private final GlobalCorpusEmbeddingService embeddingService;

    @PostMapping("/api/local/corpus/embeddings/run")
    public EmbeddingRunResponse run(@RequestParam(defaultValue = "10") int limit) {
        int embeddedCount = embeddingService.embedPendingRecords(limit);
        return new EmbeddingRunResponse(embeddedCount);
    }

    public record EmbeddingRunResponse(
        int embeddedCount
    ) {
    }
}
