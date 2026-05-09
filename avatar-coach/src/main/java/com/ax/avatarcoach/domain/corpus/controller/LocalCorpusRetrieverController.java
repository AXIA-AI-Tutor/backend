package com.ax.avatarcoach.domain.corpus.controller;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchCondition;
import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchResult;
import com.ax.avatarcoach.domain.corpus.service.GlobalCorpusRetrieverService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("local")
@RestController
@RequiredArgsConstructor
public class LocalCorpusRetrieverController {

    private final GlobalCorpusRetrieverService retrieverService;

    @GetMapping("/api/local/corpus/retrieve")
    public List<CorpusSearchResult> retrieve(
        @RequestParam String query,
        @RequestParam(required = false) String target,
        @RequestParam(required = false) List<String> recordTypes,
        @RequestParam(required = false) String difficulty,
        @RequestParam(defaultValue = "5") int limit
    ) {
        CorpusSearchCondition condition = new CorpusSearchCondition(
            query,
            target,
            recordTypes,
            difficulty,
            limit
        );

        return retrieverService.search(condition);
    }
}
