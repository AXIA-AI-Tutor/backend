package com.ax.avatarcoach.domain.session.entity;

import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "session_events")
public class SessionEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private SessionEventType eventType;

    @Column(name = "event_order", nullable = false)
    private Integer eventOrder;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    public static SessionEvent create(
        Session session,
        SessionEventType eventType,
        Integer eventOrder,
        String payloadJson
    ) {
        SessionEvent sessionEvent = new SessionEvent();
        sessionEvent.session = session;
        sessionEvent.eventType = eventType;
        sessionEvent.eventOrder = eventOrder;
        sessionEvent.payloadJson = payloadJson;
        return sessionEvent;
    }
}
