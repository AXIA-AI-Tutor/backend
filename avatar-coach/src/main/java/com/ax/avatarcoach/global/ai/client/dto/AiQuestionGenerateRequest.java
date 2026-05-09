package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

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

    @JsonProperty("rag_context")
    List<RagContextItem> ragContext
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

    public record RagContextItem(
        String source,

        @JsonProperty("record_id")
        String recordId,

        @JsonProperty("record_type")
        String recordType,

        String target,

        String difficulty,

        @JsonProperty("followup_strategy")
        String followupStrategy,

        @JsonProperty("topic_path")
        List<String> topicPath,

        Double score,

        String text,

        Map<String, Object> rubric,

        @JsonProperty("followup_pattern_ext")
        Map<String, Object> followupPatternExt,

        @JsonProperty("source_refs")
        List<Map<String, Object>> sourceRefs
    ) {
    }
}
