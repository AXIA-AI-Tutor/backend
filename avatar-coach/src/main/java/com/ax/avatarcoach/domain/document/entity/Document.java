package com.ax.avatarcoach.domain.document.entity;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Table(name = "documents")
public class Document extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 30)
    private DocumentType docType;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", nullable = false, length = 20)
    private StorageProvider storageProvider;

    @Column(name = "storage_bucket", nullable = false, length = 255)
    private String storageBucket;

    @Column(name = "storage_path", nullable = false, length = 1000)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 20)
    private UploadStatus uploadStatus;

    @Column(name = "upload_url_expires_at")
    private LocalDateTime uploadUrlExpiresAt;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus status;

    public static Document create(
        User user,
        Session session,
        DocumentType docType,
        String originalFileName,
        String fileType,
        Long fileSize,
        StorageProvider storageProvider,
        String storageBucket,
        String storagePath,
        LocalDateTime uploadUrlExpiresAt
    ) {
        Document document = new Document();
        document.user = user;
        document.session = session;
        document.docType = docType;
        document.originalFileName = originalFileName;
        document.fileType = fileType;
        document.fileSize = fileSize;
        document.storageProvider = storageProvider;
        document.storageBucket = storageBucket;
        document.storagePath = storagePath;
        document.uploadStatus = UploadStatus.PENDING;
        document.uploadUrlExpiresAt = uploadUrlExpiresAt;
        document.status = DocumentStatus.CREATED;
        return document;
    }

    public void markUploaded(LocalDateTime uploadedAt) {
        this.uploadStatus = UploadStatus.UPLOADED;
        this.status = DocumentStatus.READY_FOR_AI;
        this.uploadedAt = uploadedAt;
    }

    public void markUploadFailed() {
        this.uploadStatus = UploadStatus.FAILED;
        this.status = DocumentStatus.FAILED;
    }

    public void markProcessing() {
        this.status = DocumentStatus.PROCESSING;
    }

    public void completeSummary(String summary) {
        this.summary = summary;
        this.status = DocumentStatus.COMPLETED;
    }

    public void markSummaryFailed() {
        this.status = DocumentStatus.FAILED;
    }

    public boolean isOwnedBy(User user) {
        return this.user != null && user != null && Objects.equals(this.user.getId(), user.getId());
    }
}
