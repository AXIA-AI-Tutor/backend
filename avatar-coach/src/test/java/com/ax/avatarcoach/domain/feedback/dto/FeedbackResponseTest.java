package com.ax.avatarcoach.domain.feedback.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeTtsAudioUrlAsSnakeCase() throws Exception {
        FeedbackResponse response = new FeedbackResponse(
            1L,
            2L,
            "summary",
            "evidence",
            "example",
            "https://cdn.example.com/feedback.wav",
            80,
            70,
            90,
            60,
            null
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"tts_audio_url\":\"https://cdn.example.com/feedback.wav\"");
    }
}
