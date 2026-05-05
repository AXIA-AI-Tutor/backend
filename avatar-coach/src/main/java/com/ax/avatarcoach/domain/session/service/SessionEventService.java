package com.ax.avatarcoach.domain.session.service;

import com.ax.avatarcoach.domain.session.dto.SessionEventResponse;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionEvent;
import com.ax.avatarcoach.domain.session.entity.SessionEventType;
import com.ax.avatarcoach.domain.session.repository.SessionEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionEventService {

    private final SessionEventRepository sessionEventRepository;

    @Transactional
    public void recordEvent(
        Session session,
        SessionEventType eventType,
        String payloadJson
    ) {
        int nextEventOrder = sessionEventRepository.countBySession(session) + 1;

        SessionEvent sessionEvent = SessionEvent.create(
            session,
            eventType,
            nextEventOrder,
            payloadJson
        );

        sessionEventRepository.save(sessionEvent);
    }

    public List<SessionEventResponse> getSessionEvents(Session session) {
        return sessionEventRepository.findAllBySessionOrderByEventOrderAsc(session).stream()
            .map(SessionEventResponse::from)
            .toList();
    }
}
