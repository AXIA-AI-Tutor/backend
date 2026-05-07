package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionDifficulty;
import com.ax.avatarcoach.domain.session.entity.SessionMode;
import com.ax.avatarcoach.domain.session.entity.SessionStatus;
import com.ax.avatarcoach.domain.session.entity.SessionTarget;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "세션 응답")
public record SessionResponse (
    @Schema(description = "세션 ID", example = "1")
    Long id,

    @Schema(description = "사용자 ID", example = "1")
    Long userId,

    @Schema(description = "연습 모드", example = "INTERVIEW")
    SessionMode mode,

    @Schema(description = "연습 대상 직무", example = "FRONTEND")
    SessionTarget target,

    @Schema(description = "연습 난이도", example = "EASY")
    SessionDifficulty difficulty,

    @Schema(description = "질문당 답변 제한 시간(초)", example = "120")
    Integer answerTimeLimitSec,

    @Schema(description = "세션 상태", example = "READY")
    SessionStatus status,

    @Schema(description = "세션 시작 시각", example = "2026-05-05T10:00:00")
    LocalDateTime startedAt,

    @Schema(description = "세션 종료 시각", example = "2026-05-05T10:30:00")
    LocalDateTime completedAt,

    @Schema(description = "세션 생성 시각", example = "2026-05-05T09:55:00")
    LocalDateTime createdAt
) {

    public static SessionResponse from(Session session) {
        return new SessionResponse(
            session.getId(),
            session.getUser().getId(),
            session.getMode(),
            session.getTarget(),
            session.getDifficulty(),
            session.getAnswerTimeLimitSec(),
            session.getStatus(),
            session.getStartedAt(),
            session.getCompletedAt(),
            session.getCreatedAt()
        );
    }
}
