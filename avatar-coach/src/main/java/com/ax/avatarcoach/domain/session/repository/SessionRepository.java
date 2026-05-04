package com.ax.avatarcoach.domain.session.repository;

import com.ax.avatarcoach.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
