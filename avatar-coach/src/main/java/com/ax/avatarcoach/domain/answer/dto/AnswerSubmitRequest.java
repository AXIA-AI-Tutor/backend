package com.ax.avatarcoach.domain.answer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Schema(description = "답변 제출 및 피드백 생성 요청")
public record AnswerSubmitRequest(
    @Schema(description = "답변 평가 기준이 되는 질문 문장", example = "백엔드 개발자에 지원한 이유를 설명해 주세요.")
    @NotBlank
    String questionText,

    @Schema(description = "STT로 전사할 webm 음성 파일", type = "string", format = "binary")
    @NotNull
    MultipartFile file,

    @Schema(description = "아이컨택 점수", example = "80")
    @Min(0)
    @Max(100)
    Integer eyeContactScore,

    @Schema(description = "자세 점수", example = "75")
    @Min(0)
    @Max(100)
    Integer postureScore,

    @Schema(description = "답변 시작 시각", example = "2026-05-06T16:10:00")
    LocalDateTime startedAt,

    @Schema(description = "답변 종료 시각", example = "2026-05-06T16:11:27")
    LocalDateTime endedAt
) {
}
