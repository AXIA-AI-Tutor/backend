package com.ax.avatarcoach.domain.answer.dto;

import com.ax.avatarcoach.domain.answer.entity.Answer;
import com.ax.avatarcoach.domain.answer.entity.SttStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "답변 응답")
public record AnswerResponse(
    @Schema(description = "답변 ID", example = "1")
    Long answerId,

    @Schema(description = "세션 ID", example = "1")
    Long sessionId,

    @Schema(description = "질문 텍스트")
    String questionText,

    @Schema(description = "답변 전사 텍스트")
    String transcript,

    @Schema(description = "답변 길이(초)")
    Integer durationSec,

    @Schema(description = "말하기 속도")
    BigDecimal speechRate,

    @Schema(description = "침묵 구간 수")
    Integer silenceCount,

    @Schema(description = "추임새 단어 수")
    Integer fillerWordCount,

    @Schema(description = "아이 컨택 점수")
    Integer eyeContactScore,

    @Schema(description = "자세 점수")
    Integer postureScore,

    @Schema(description = "STT 상태")
    SttStatus sttStatus,

    @Schema(description = "답변 시작 시각")
    LocalDateTime startedAt,

    @Schema(description = "답변 종료 시각")
    LocalDateTime endedAt,

    @Schema(description = "생성 시각")
    LocalDateTime createdAt
) {
    public static AnswerResponse from(Answer answer) {
        return new AnswerResponse(
            answer.getId(),
            answer.getSession().getId(),
            answer.getQuestionText(),
            answer.getTranscript(),
            answer.getDurationSec(),
            answer.getSpeechRate(),
            answer.getSilenceCount(),
            answer.getFillerWordCount(),
            answer.getEyeContactScore(),
            answer.getPostureScore(),
            answer.getSttStatus(),
            answer.getStartedAt(),
            answer.getEndedAt(),
            answer.getCreatedAt()
        );
    }
}
