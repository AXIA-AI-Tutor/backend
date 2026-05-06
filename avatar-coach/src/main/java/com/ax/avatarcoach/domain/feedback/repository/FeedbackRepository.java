package com.ax.avatarcoach.domain.feedback.repository;

import com.ax.avatarcoach.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findAllByAnswerIdOrderByCreatedAtAsc(Long answerId);
}
