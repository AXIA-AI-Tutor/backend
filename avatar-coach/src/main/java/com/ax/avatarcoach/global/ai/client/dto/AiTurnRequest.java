package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record AiTurnRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("session_id")
    Long sessionId,

    @JsonProperty("answer_id")
    Long answerId,

    String mode,

    @JsonProperty("question_text")
    String questionText,

    String transcript,
    MultipartFile file,

    @JsonProperty("vision_metrics")
    String visionMetrics,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("rag_context")
    List<AiRagContextItem> ragContext
) {
}
