package com.ax.avatarcoach.domain.report.entity;

import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "reports")
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private Session session;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(columnDefinition = "text")
    private String strengths;

    @Column(columnDefinition = "text")
    private String improvements;

    public static Report create(
        Session session,
        Integer totalScore,
        String strengths,
        String improvements
    ) {
        Report report = new Report();
        report.session = session;
        report.totalScore = totalScore;
        report.strengths = strengths;
        report.improvements = improvements;
        return report;
    }
}
