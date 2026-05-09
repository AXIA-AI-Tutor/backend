package com.ax.avatarcoach.domain.corpus.repository;

import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GlobalCorpusRecordRepository extends JpaRepository<GlobalCorpusRecord, Long> {

    Optional<GlobalCorpusRecord> findByRecordId(String recordId);

    boolean existsByRecordId(String recordId);

    @Query(
        value = """
            SELECT *
            FROM global_corpus_records
            WHERE embedding IS NULL
              AND embedding_text_ko IS NOT NULL
              AND embedding_text_ko <> ''
            ORDER BY id
            LIMIT :limit
            """,
        nativeQuery = true
    )
    List<GlobalCorpusRecord> findPendingEmbeddingRecords(@Param("limit") int limit);
}
