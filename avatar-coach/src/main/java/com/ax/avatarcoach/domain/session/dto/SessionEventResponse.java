package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.SessionEvent;
import com.ax.avatarcoach.domain.session.entity.SessionEventType;

import java.time.LocalDateTime;

public record SessionEventResponse (
    Long id,
    Long sessionId,
    SessionEventType eventType,
    Integer eventOrder,
    String payloadJson,
    LocalDateTime createdAt
) {

    public static SessionEventResponse from(SessionEvent sessionEvent) {
        return new SessionEventResponse(
            sessionEvent.getId(),
            sessionEvent.getSession().getId(),
            sessionEvent.getEventType(),
            sessionEvent.getEventOrder(),
            sessionEvent.getPayloadJson(),
            sessionEvent.getCreatedAt()
        );
    }
}
