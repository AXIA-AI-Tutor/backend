package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusRecord;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusEmbeddingJdbcRepository;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Profile("local")
@Service
@RequiredArgsConstructor
@Transactional
public class GlobalCorpusEmbeddingService {

    private final GlobalCorpusRecordRepository recordRepository;
    private final GlobalCorpusEmbeddingJdbcRepository embeddingJdbcRepository;
    private final CorpusEmbeddingClient embeddingClient;

    public int embedPendingRecords(int limit) {
        List<GlobalCorpusRecord> records = recordRepository.findPendingEmbeddingRecords(limit);

        for (GlobalCorpusRecord record : records) {
            List<Double> embedding = embeddingClient.embed(record.getEmbeddingTextKo());
            embeddingJdbcRepository.updateEmbedding(record.getId(), embedding);
        }

        return records.size();
    }
}
