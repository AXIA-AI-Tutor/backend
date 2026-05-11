package com.ax.avatarcoach.domain.feedback.entity;

import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "feedbacks")
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String evidence;

    @Column(name = "improvement_example", columnDefinition = "text")
    private String improvementExample;

    @Column(name = "tts_audio_url", columnDefinition = "text")
    private String ttsAudioUrl;

    @Column(name = "structure_score")
    private Integer structureScore;

    @Column(name = "specificity_score")
    private Integer specificityScore;

    @Column(name = "relevance_score")
    private Integer relevanceScore;

    @Column(name = "delivery_score")
    private Integer deliveryScore;

    public static Feedback create(
        Answer answer,
        String summary,
        String evidence,
        String improvementExample,
        String ttsAudioUrl,
        Integer structureScore,
        Integer specificityScore,
        Integer relevanceScore,
        Integer deliveryScore
    ) {
        Feedback feedback = new Feedback();
        feedback.answer = answer;
        feedback.summary = summary;
        feedback.evidence = evidence;
        feedback.improvementExample = improvementExample;
        feedback.ttsAudioUrl = ttsAudioUrl;
        feedback.structureScore = structureScore;
        feedback.specificityScore = specificityScore;
        feedback.relevanceScore = relevanceScore;
        feedback.deliveryScore = deliveryScore;
        return feedback;
    }
}
