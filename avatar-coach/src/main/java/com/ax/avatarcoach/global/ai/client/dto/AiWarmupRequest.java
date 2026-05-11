package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiWarmupRequest(
    @JsonProperty("components")
    List<String> components
) {
    public static AiWarmupRequest defaultSessionWarmup() {
        return new AiWarmupRequest(List.of("stt", "embedding", "llm"));
    }
}
