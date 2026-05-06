package com.ax.avatarcoach.domain.report.dto;

import com.ax.avatarcoach.domain.report.entity.Report;

import java.time.LocalDateTime;

public record ReportResponse(
    Long reportId,
    Long sessionId,
    Integer totalScore,
    String strengths,
    String improvements,
    LocalDateTime createdAt
) {
    public static ReportResponse from(Report report) {
        return new ReportResponse(
            report.getId(),
            report.getSession().getId(),
            report.getTotalScore(),
            report.getStrengths(),
            report.getImprovements(),
            report.getCreatedAt()
        );
    }
}
