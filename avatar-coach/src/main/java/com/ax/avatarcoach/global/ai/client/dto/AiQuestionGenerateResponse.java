package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * FastAPI가 준 JSON 응답을 Java 객체로 받는 역할의 DTO
 */
public record AiQuestionGenerateResponse(
    @JsonProperty("question_text")
    String questionText,

    @JsonProperty("question_intent")
    String questionIntent,

    @JsonProperty("tts_audio_url")
    String ttsAudioUrl,

    @JsonProperty("latency_ms")
    Integer latencyMs,

    @JsonProperty("fallback_components")
    List<String> fallbackComponents
) {
}
