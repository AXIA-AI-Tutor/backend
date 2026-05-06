package com.ax.avatarcoach.domain.document.repository;

import com.ax.avatarcoach.domain.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findAllBySessionIdAndUserIdOrderByCreatedAtDesc(Long sessionId, Long userId, Pageable pageable);
}
