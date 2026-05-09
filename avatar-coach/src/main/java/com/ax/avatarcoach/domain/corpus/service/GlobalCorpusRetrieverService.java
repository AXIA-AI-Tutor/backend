package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.entity.CorpusSearchCondition;
import com.ax.avatarcoach.domain.corpus.entity.CorpusSearchResult;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusRetrieverJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile("local")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GlobalCorpusRetrieverService {

    private final CorpusEmbeddingClient embeddingClient;
    private final GlobalCorpusRetrieverJdbcRepository retrieverRepository;

    public List<CorpusSearchResult> search(CorpusSearchCondition condition) {
        List<Double> queryEmbedding = embeddingClient.embed(condition.query());
        return retrieverRepository.search(condition, queryEmbedding);
    }
}
