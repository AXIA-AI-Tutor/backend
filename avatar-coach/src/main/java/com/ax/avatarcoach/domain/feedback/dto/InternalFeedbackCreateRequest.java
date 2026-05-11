package com.ax.avatarcoach.domain.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "내부 피드백 저장 요청")
public record InternalFeedbackCreateRequest(
    @Schema(description = "피드백 요약", example = "핵심 경험은 잘 전달했지만 근거가 부족합니다.")
    @NotBlank
    String summary,

    @Schema(description = "근거", example = "프로젝트 수치와 역할이 명확하지 않았습니다.")
    String evidence,

    @Schema(description = "개선 예시", example = "저는 결제 지연 문제를 분석하여 평균 응답시간을 30% 줄였습니다.")
    String improvementExample,

    @Schema(description = "피드백 음성 파일 URL", example = "https://cdn.example.com/feedback/123.wav")
    String ttsAudioUrl,

    @Schema(description = "구조 점수", example = "78")
    @Min(0)
    @Max(100)
    Integer structureScore,

    @Schema(description = "구체성 점수", example = "72")
    @Min(0)
    @Max(100)
    Integer specificityScore,

    @Schema(description = "관련성 점수", example = "81")
    @Min(0)
    @Max(100)
    Integer relevanceScore,

    @Schema(description = "전달력 점수", example = "69")
    @Min(0)
    @Max(100)
    Integer deliveryScore
) {
}
