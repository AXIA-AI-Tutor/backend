package com.ax.avatarcoach.domain.document.dto;

import com.ax.avatarcoach.domain.document.entity.Document;
import com.ax.avatarcoach.domain.document.entity.DocumentStatus;
import com.ax.avatarcoach.domain.document.entity.DocumentType;
import com.ax.avatarcoach.domain.document.entity.StorageProvider;
import com.ax.avatarcoach.domain.document.entity.UploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "문서 응답")
public record DocumentResponse(
    @Schema(description = "문서 ID", example = "1")
    Long id,

    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "세션 ID", example = "1")
    Long sessionId,

    @Schema(description = "문서 타입", example = "RESUME")
    DocumentType docType,

    @Schema(description = "원본 파일명", example = "resume.pdf")
    String originalFileName,

    @Schema(description = "파일 MIME 타입", example = "application/pdf")
    String fileType,

    @Schema(description = "파일 크기(bytes)", example = "120482")
    Long fileSize,

    @Schema(description = "저장소 제공자", example = "GCS")
    StorageProvider storageProvider,

    @Schema(description = "저장 버킷", example = "avatar-coach-dev")
    String storageBucket,

    @Schema(description = "저장 경로", example = "documents/1/resume.pdf")
    String storagePath,

    @Schema(description = "업로드 상태", example = "PENDING")
    UploadStatus uploadStatus,

    @Schema(description = "업로드 URL 만료 시각", example = "2026-05-05T10:10:00")
    LocalDateTime uploadUrlExpiresAt,

    @Schema(description = "실제 업로드 완료 시각", example = "2026-05-05T10:03:00")
    LocalDateTime uploadedAt,

    @Schema(description = "요약", example = "이 문서는 백엔드 개발자 이력서입니다.")
    String summary,

    @Schema(description = "문서 처리 상태", example = "CREATED")
    DocumentStatus status,

    @Schema(description = "생성 시각", example = "2026-05-05T10:00:00")
    LocalDateTime createdAt
) {

    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
            document.getId(),
            document.getUser().getId(),
            document.getSession().getId(),
            document.getDocType(),
            document.getOriginalFileName(),
            document.getFileType(),
            document.getFileSize(),
            document.getStorageProvider(),
            document.getStorageBucket(),
            document.getStoragePath(),
            document.getUploadStatus(),
            document.getUploadUrlExpiresAt(),
            document.getUploadedAt(),
            document.getSummary(),
            document.getStatus(),
            document.getCreatedAt()
        );
    }
}
