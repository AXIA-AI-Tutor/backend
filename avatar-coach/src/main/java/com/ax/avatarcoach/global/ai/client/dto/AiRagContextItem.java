package com.ax.avatarcoach.global.ai.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Spring이 pgvector에서 찾은 corpus 검색 결과를
 * FastAPI prompt에 넣기 좋은 안전한 형태로 줄인 객체
 */
public record AiRagContextItem(
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
