package com.ax.avatarcoach.domain.document.dto;

import com.ax.avatarcoach.domain.document.entity.StorageProvider;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "문서 업로드 URL 발급 응답")
public record DocumentUploadUrlResponse(
    @Schema(description = "문서 ID", example = "1")
    Long documentId,

    @Schema(description = "업로드 URL")
    String uploadUrl,

    @Schema(description = "HTTP 업로드 메서드", example = "PUT")
    String method,

    @Schema(description = "저장소 제공자", example = "GCS")
    StorageProvider storageProvider,

    @Schema(description = "저장 버킷", example = "avatar-coach-dev")
    String storageBucket,

    @Schema(description = "저장 경로")
    String storagePath,

    @Schema(description = "업로드 URL 만료 시각", example = "2026-05-05T10:10:00")
    LocalDateTime uploadUrlExpiresAt,

    @Schema(description = "업로드 시 반드시 포함해야 하는 헤더")
    Map<String, String> requiredHeaders
) {
}
