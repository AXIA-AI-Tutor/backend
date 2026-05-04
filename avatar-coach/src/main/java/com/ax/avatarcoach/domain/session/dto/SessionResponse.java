package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionDifficulty;
import com.ax.avatarcoach.domain.session.entity.SessionMode;
import com.ax.avatarcoach.domain.session.entity.SessionStatus;
import com.ax.avatarcoach.domain.session.entity.SessionTarget;

import java.time.LocalDateTime;

public record SessionResponse (
    Long id,
    Long userId,
    SessionMode mode,
    SessionTarget target,
    SessionDifficulty difficulty,
    Integer answerTimeLimitSec,
    SessionStatus status,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
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
