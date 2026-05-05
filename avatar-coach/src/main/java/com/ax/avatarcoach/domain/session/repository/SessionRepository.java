package com.ax.avatarcoach.domain.session.repository;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByIdAndUser(Long id, User user);

    List<Session> findAllByUserOrderByCreatedAtDesc(User user);
}
