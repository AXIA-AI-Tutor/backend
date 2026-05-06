package com.ax.avatarcoach.domain.answer.repository;

import com.ax.avatarcoach.domain.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
