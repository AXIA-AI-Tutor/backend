package com.ax.avatarcoach.domain.feedback.dto;

import com.ax.avatarcoach.domain.feedback.entity.Feedback;

import java.time.LocalDateTime;

public record FeedbackResponse(
    Long feedbackId,
    Long answerId,
    String summary,
    String evidence,
    String improvementExample,
    Integer structureScore,
    Integer specificityScore,
    Integer relevanceScore,
    Integer deliveryScore,
    LocalDateTime createdAt
) {
    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
            feedback.getId(),
            feedback.getAnswer().getId(),
            feedback.getSummary(),
            feedback.getEvidence(),
            feedback.getImprovementExample(),
            feedback.getStructureScore(),
            feedback.getSpecificityScore(),
            feedback.getRelevanceScore(),
            feedback.getDeliveryScore(),
            feedback.getCreatedAt()
        );
    }
}
