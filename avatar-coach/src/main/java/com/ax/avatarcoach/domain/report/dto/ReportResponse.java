package com.ax.avatarcoach.domain.report.dto;

import com.ax.avatarcoach.domain.report.entity.Report;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "세션 리포트 응답")
public record ReportResponse(
    @Schema(description = "리포트 ID", example = "1")
    Long reportId,

    @Schema(description = "리포트가 연결된 세션 ID", example = "10")
    Long sessionId,

    @Schema(description = "AI가 산출한 전체 점수", example = "82")
    Integer totalScore,

    @Schema(description = "세션 전체 기준 강점 요약", example = "답변의 구조가 안정적이고 핵심 경험을 명확히 설명했습니다.")
    String strengths,

    @Schema(description = "세션 전체 기준 개선점 요약", example = "답변마다 구체적인 수치와 결과를 더 보강하면 좋습니다.")
    String improvements,

    @Schema(description = "리포트 생성 시각", example = "2026-05-07T10:30:00")
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
