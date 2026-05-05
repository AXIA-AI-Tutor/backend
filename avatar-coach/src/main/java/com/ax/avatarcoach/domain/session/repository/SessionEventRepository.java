package com.ax.avatarcoach.domain.session.repository;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionEventRepository extends JpaRepository<SessionEvent, Long> {

    /**
     * 특정 세션의 이벤트 목록을 순서대로 조회
     */
    List<SessionEvent> findAllBySessionOrderByEventOrderAsc(Session session);

    /**
     * 다음 이벤트의 eventOrder를 계산할 때 사용
     */
    int countBySession(Session session);
}
