package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiQuestionGenerateRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("session_id")
    Long sessionId,

    String mode,

    String target,

    String difficulty,

    Integer answerTimeLimitSec,

    @JsonProperty("question_index")
    Integer questionIndex,

    @JsonProperty("previous_questions")
    List<String> previousQuestions,

    @JsonProperty("previous_turns")
    List<PreviousTurn> previousTurns,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("rag_context")
    List<AiRagContextItem> ragContext
) {
    public record PreviousTurn(
        @JsonProperty("answer_id")
        Long answerId,

        @JsonProperty("question_index")
        Integer questionIndex,

        @JsonProperty("question_text")
        String questionText,

        String transcript,

        @JsonProperty("feedback_summary")
        String feedbackSummary,

        @JsonProperty("improvement_example")
        String improvementExample
    ) {
    }
}
