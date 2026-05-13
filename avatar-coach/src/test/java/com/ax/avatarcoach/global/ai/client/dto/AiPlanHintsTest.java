package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AiPlanHintsTest {

    @Test
    void isEmptyIsNotSerializedAsEmptyProperty() throws Exception {
        AiPlanHints planHints = new AiPlanHints(
            List.of("refresh token"),
            List.of("security trade-off"),
            List.of("tell me more"),
            List.of("How did you decide token expiry?")
        );

        String json = new ObjectMapper().writeValueAsString(planHints);

        assertThat(json).contains("\"anchor_candidates\"");
        assertThat(json).doesNotContain("\"empty\"");
    }
}
