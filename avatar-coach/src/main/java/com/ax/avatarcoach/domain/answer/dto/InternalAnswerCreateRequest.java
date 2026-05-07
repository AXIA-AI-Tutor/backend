package com.ax.avatarcoach.domain.answer.dto;

import com.ax.avatarcoach.domain.answer.entity.SttStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "내부 답변 저장 요청")
public record InternalAnswerCreateRequest(
    @Schema(description = "질문 텍스트", example = "지원 동기를 말씀해 주세요.")
    @NotBlank
    String questionText,

    @Schema(description = "답변 전사 텍스트", example = "저는 사용자 문제 해결에 관심이 많습니다.")
    @NotBlank
    String transcript,

    @Schema(description = "답변 길이(초)", example = "45")
    @PositiveOrZero
    Integer durationSec,

    @Schema(description = "말하기 속도", example = "4.20")
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal speechRate,

    @Schema(description = "침묵 구간 수", example = "2")
    @PositiveOrZero
    Integer silenceCount,

    @Schema(description = "추임새 단어 수", example = "3")
    @PositiveOrZero
    Integer fillerWordCount,

    @Schema(description = "아이 컨택 점수", example = "85")
    @Min(0)
    @Max(100)
    Integer eyeContactScore,

    @Schema(description = "자세 점수", example = "90")
    @Min(0)
    @Max(100)
    Integer postureScore,

    @Schema(description = "STT 처리 상태", example = "COMPLETED")
    SttStatus sttStatus,

    @Schema(description = "답변 시작 시각", example = "2026-05-05T10:00:00")
    LocalDateTime startedAt,

    @Schema(description = "답변 종료 시각", example = "2026-05-05T10:00:45")
    LocalDateTime endedAt
) {
}
