package com.ax.avatarcoach.domain.session.entity;

import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "sessions")
public class Session extends BaseTimeEntity {

    private static final int DEFAULT_ANSWER_TIME_LIMIT_SEC = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionTarget target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionDifficulty difficulty;

    @Column(name = "answer_time_limit_sec", nullable = false)
    private Integer answerTimeLimitSec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    /**
     * Session 객체를 안전하게 생성하는 정적 팩토리 메서드.
     * 사용 이유 : setter를 막고, 생성 규칙을 한 곳에 모으기 위해서
     */
    public static Session create(
        User user,
        SessionMode mode,
        SessionTarget target,
        SessionDifficulty difficulty
    ) {
        Session session = new Session();
        session.user = user;
        session.mode = mode;
        session.target = target;
        session.difficulty = difficulty;
        session.answerTimeLimitSec = DEFAULT_ANSWER_TIME_LIMIT_SEC;
        session.status = SessionStatus.READY;
        return session;
    }
}
