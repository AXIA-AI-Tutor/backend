package com.ax.avatarcoach.domain.document.repository;

import com.ax.avatarcoach.domain.document.entity.Document;
import com.ax.avatarcoach.domain.document.entity.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findAllBySessionIdAndUserIdOrderByCreatedAtDesc(Long sessionId, Long userId, Pageable pageable);

    boolean existsBySessionIdAndUserIdAndStatus(Long sessionId, Long userId, DocumentStatus status);

    List<Document> findAllBySessionIdAndUserIdAndStatusOrderByCreatedAtDesc(
        Long sessionId,
        Long userId,
        DocumentStatus status
    );
}
