package com.ax.avatarcoach.domain.report.repository;

import com.ax.avatarcoach.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findBySessionId(Long sessionId);

    boolean existsBySessionId(Long sessionId);
}
