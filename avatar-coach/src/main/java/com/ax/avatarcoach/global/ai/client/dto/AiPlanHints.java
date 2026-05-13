package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AiPlanHints(
    @JsonProperty("anchor_candidates")
    List<String> anchorCandidates,

    @JsonProperty("must_check")
    List<String> mustCheck,

    @JsonProperty("avoid_phrases")
    List<String> avoidPhrases,

    @JsonProperty("good_question_examples")
    List<String> goodQuestionExamples
) {
    @JsonIgnore
    public boolean isEmpty() {
        return isBlank(anchorCandidates)
            && isBlank(mustCheck)
            && isBlank(avoidPhrases)
            && isBlank(goodQuestionExamples);
    }

    private boolean isBlank(List<String> values) {
        return values == null || values.isEmpty();
    }
}
