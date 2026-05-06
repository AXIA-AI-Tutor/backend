package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record AiReportGenerateRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("session_id")
    Long sessionId,

    String mode,

    String target,

    List<AnswerItem> answers,

    List<FeedbackItem> feedbacks
) {
    public record AnswerItem(
        @JsonProperty("answer_id")
        Long answerId,

        @JsonProperty("question_text")
        String questionText,

        String transcript,

        @JsonProperty("duration_sec")
        Integer durationSec,

        @JsonProperty("audio_metrics")
        Map<String, Object> audioMetrics,

        @JsonProperty("vision_metrics")
        Map<String, Object> visionMetrics
    ) {
    }

    public record FeedbackItem(
        @JsonProperty("answer_id")
        Long answerId,

        String summary,

        String evidence,

        @JsonProperty("improvement_example")
        String improvementExample,

        @JsonProperty("structure_score")
        Integer structureScore,

        @JsonProperty("specificity_score")
        Integer specificityScore,

        @JsonProperty("relevance_score")
        Integer relevanceScore,

        @JsonProperty("delivery_score")
        Integer deliveryScore
    ) {
    }
}
