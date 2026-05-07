package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * FastAPI가 준 JSON 응답을 Java 객체로 받는 역할의 DTO
 */
@Schema(description = "AI 질문 생성 응답")
public record AiQuestionGenerateResponse(
    @Schema(description = "AI가 생성한 질문 문장입니다.", example = "방금 답변에서 언급한 Redis 캐싱 전략을 선택한 이유와 대안을 비교해 설명해 주세요.")
    @JsonProperty("question_text")
    String questionText,

    @Schema(description = "질문으로 확인하려는 평가 의도입니다.", example = "이전 답변의 기술적 판단 근거와 대안 비교 능력을 확인합니다.")
    @JsonProperty("question_intent")
    String questionIntent,

    @Schema(description = "질문 TTS 오디오 파일 URL입니다. 없을 수 있습니다.", example = "/static/tts/example.wav")
    @JsonProperty("tts_audio_url")
    String ttsAudioUrl,

    @Schema(description = "AI 서버의 질문 생성 처리 시간입니다. 단위는 ms입니다.", example = "1200")
    @JsonProperty("latency_ms")
    Integer latencyMs,

    @Schema(description = "fallback 또는 mock으로 처리된 AI 구성요소 목록입니다.", example = "[\"llm\", \"tts\"]")
    @JsonProperty("fallback_components")
    List<String> fallbackComponents
) {
}
