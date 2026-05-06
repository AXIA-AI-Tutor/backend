package com.ax.avatarcoach.domain.answer.dto;

import com.ax.avatarcoach.domain.answer.entity.SttStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "답변 제출 및 피드백 생성 요청")
public record AnswerSubmitRequest(
    @Schema(description = "답변 평가 기준이 되는 질문 문장", example = "백엔드 개발자에 지원한 이유를 설명해 주세요.")
    @NotBlank
    String questionText,

    @Schema(description = "사용자 답변 전사 텍스트", example = "저는 API 설계와 데이터 모델링 경험을 바탕으로...")
    @NotBlank
    String transcript,

    @Schema(description = "답변 길이(초)", example = "87")
    @PositiveOrZero
    Integer durationSec,

    @Schema(description = "말하기 속도", example = "120.5")
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal speechRate,

    @Schema(description = "침묵 구간 수", example = "2")
    @PositiveOrZero
    Integer silenceCount,

    @Schema(description = "추임새 단어 수", example = "3")
    @PositiveOrZero
    Integer fillerWordCount,

    @Schema(description = "아이컨택 점수", example = "80")
    @Min(0)
    @Max(100)
    Integer eyeContactScore,

    @Schema(description = "자세 점수", example = "75")
    @Min(0)
    @Max(100)
    Integer postureScore,

    @Schema(description = "STT 처리 상태", example = "COMPLETED")
    SttStatus sttStatus,

    @Schema(description = "답변 시작 시각", example = "2026-05-06T16:10:00")
    LocalDateTime startedAt,

    @Schema(description = "답변 종료 시각", example = "2026-05-06T16:11:27")
    LocalDateTime endedAt
) {
}
