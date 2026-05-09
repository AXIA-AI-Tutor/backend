package com.ax.avatarcoach.domain.corpus.repository;

import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlobalCorpusSourceRepository extends JpaRepository<GlobalCorpusSource, Long> {

    Optional<GlobalCorpusSource> findBySourceKey(String sourceKey);

    boolean existsBySourceKey(String sourceKey);
}
