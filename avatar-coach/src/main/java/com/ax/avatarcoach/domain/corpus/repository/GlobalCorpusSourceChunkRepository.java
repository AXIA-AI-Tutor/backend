package com.ax.avatarcoach.domain.corpus.repository;

import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusSourceChunk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalCorpusSourceChunkRepository extends JpaRepository<GlobalCorpusSourceChunk, Long> {

    boolean existsBySourceChunkId(String sourceChunkId);
}
