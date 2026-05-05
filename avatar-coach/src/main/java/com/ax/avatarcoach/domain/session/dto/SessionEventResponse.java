package com.ax.avatarcoach.domain.session.dto;

import com.ax.avatarcoach.domain.session.entity.SessionEvent;
import com.ax.avatarcoach.domain.session.entity.SessionEventType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "세션 이벤트 응답")
public record SessionEventResponse (
    @Schema(description = "이벤트 ID", example = "1")
    Long id,

    @Schema(description = "세션 ID", example = "1")
    Long sessionId,

    @Schema(description = "이벤트 타입", example = "SESSION_CREATED")
    SessionEventType eventType,

    @Schema(description = "세션 내 이벤트 순서", example = "1")
    Integer eventOrder,

    @Schema(description = "이벤트 부가 정보 JSON", example = "{\"message\":\"세션이 생성되었습니다.\"}")
    String payloadJson,

    @Schema(description = "이벤트 생성 시각", example = "2026-05-05T17:20:34")
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
