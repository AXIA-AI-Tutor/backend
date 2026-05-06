package com.ax.avatarcoach.domain.document.dto;

import com.ax.avatarcoach.domain.document.entity.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "문서 메타데이터 저장 요청")
public record DocumentMetadataCreateRequest(
    @Schema(description = "세션 ID", example = "1")
    @NotNull
    Long sessionId,

    @Schema(description = "문서 타입", example = "RESUME")
    @NotNull
    DocumentType docType,

    @Schema(description = "원본 파일명", example = "resume.pdf")
    @NotBlank
    String originalFileName,

    @Schema(description = "파일 MIME 타입", example = "application/pdf")
    @NotBlank
    String fileType,

    @Schema(description = "파일 크기(bytes)", example = "120482")
    @NotNull
    @Positive
    Long fileSize
) {
}
