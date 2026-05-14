package com.ax.avatarcoach.domain.document.repository;

import com.ax.avatarcoach.domain.document.entity.Document;
import com.ax.avatarcoach.domain.document.entity.DocumentStatus;
import com.ax.avatarcoach.domain.document.entity.UploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findAllBySessionIdAndUserIdOrderByCreatedAtDesc(Long sessionId, Long userId, Pageable pageable);

    List<Document> findAllBySessionIdAndUserIdAndUploadStatusAndStatusInOrderByCreatedAtDesc(
        Long sessionId,
        Long userId,
        UploadStatus uploadStatus,
        List<DocumentStatus> statuses
    );

    List<Document> findAllBySessionIdAndUserIdAndStatusOrderByCreatedAtDesc(
        Long sessionId,
        Long userId,
        DocumentStatus status
    );

    List<Document> findAllBySessionIdAndUserIdAndStatusInOrderByCreatedAtDesc(
        Long sessionId,
        Long userId,
        List<DocumentStatus> statuses
    );
}
