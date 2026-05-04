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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String mode;

    @Column(length = 100)
    private String target;

    @Column(length = 30)
    private String difficulty;

    @Column(name = "time_limit_sec")
    private Integer timeLimitSec;

    @Column(name = "focus_areas", length = 255)
    private String focusAreas;

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
        String mode,
        String target,
        String difficulty,
        Integer timeLimitSec,
        String focusAreas
    ) {
        Session session = new Session();
        session.user = user;
        session.mode = mode;
        session.target = target;
        session.difficulty = difficulty;
        session.timeLimitSec = timeLimitSec;
        session.focusAreas = focusAreas;
        session.status = SessionStatus.READY;
        return session;
    }
}
