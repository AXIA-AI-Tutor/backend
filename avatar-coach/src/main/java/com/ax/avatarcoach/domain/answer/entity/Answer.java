package com.ax.avatarcoach.domain.answer.entity;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "answers")
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "question_text", columnDefinition = "text")
    private String questionText;

    @Column(columnDefinition = "text")
    private String transcript;

    @Column(name = "duration_sec")
    private Integer durationSec;

    @Column(name = "speech_rate", precision = 10, scale = 2)
    private BigDecimal speechRate;

    @Column(name = "silence_count")
    private Integer silenceCount;

    @Column(name = "filler_word_count")
    private Integer fillerWordCount;

    @Column(name = "eye_contact_score")
    private Integer eyeContactScore;

    @Column(name = "posture_score")
    private Integer postureScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "stt_status", length = 30)
    private SttStatus sttStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public static Answer create(
        Session session,
        String questionText,
        String transcript,
        Integer durationSec,
        BigDecimal speechRate,
        Integer silenceCount,
        Integer fillerWordCount,
        Integer eyeContactScore,
        Integer postureScore,
        SttStatus sttStatus,
        LocalDateTime startedAt,
        LocalDateTime endedAt
    ) {
        Answer answer = new Answer();
        answer.session = session;
        answer.questionText = questionText;
        answer.transcript = transcript;
        answer.durationSec = durationSec;
        answer.speechRate = speechRate;
        answer.silenceCount = silenceCount;
        answer.fillerWordCount = fillerWordCount;
        answer.eyeContactScore = eyeContactScore;
        answer.postureScore = postureScore;
        answer.sttStatus = sttStatus;
        answer.startedAt = startedAt;
        answer.endedAt = endedAt;
        return answer;
    }

    /**
     * Answer가 AI 응답 후 transcript를 채울 수 있는 메서드
     */
    public void completeStt(String transcript) {
        this.transcript = transcript;
        this.sttStatus = SttStatus.COMPLETED;
    }
}
