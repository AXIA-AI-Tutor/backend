package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiDocumentSummaryResponse(
    @JsonProperty("summary")
    String summary
) {
}
