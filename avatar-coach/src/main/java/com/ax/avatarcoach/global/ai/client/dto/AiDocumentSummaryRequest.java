package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiDocumentSummaryRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("session_id")
    Long sessionId,

    @JsonProperty("document_id")
    Long documentId,

    @JsonProperty("doc_type")
    String docType,

    @JsonProperty("storage_bucket")
    String storageBucket,

    @JsonProperty("storage_path")
    String storagePath,

    @JsonProperty("original_file_name")
    String originalFileName,

    @JsonProperty("file_type")
    String fileType
) {
}
