package com.ax.avatarcoach.domain.corpus.repository;

import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlobalCorpusRecordRepository extends JpaRepository<GlobalCorpusRecord, Long> {

    Optional<GlobalCorpusRecord> findByRecordId(String recordId);

    boolean existsByRecordId(String recordId);
}
