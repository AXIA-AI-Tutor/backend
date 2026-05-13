package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchResult;
import com.ax.avatarcoach.global.ai.client.dto.AiRagContextItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CorpusRagContextMapper {

    private static final Set<String> RUBRIC_ALLOWED_KEYS = Set.of(
        "must_include",
        "bad_signals"
    );

    private static final Set<String> FOLLOWUP_PATTERN_EXT_ALLOWED_KEYS = Set.of(
        "anchor_examples_ko",
        "must_include_in_question",
        "negative_phrases_ko",
        "good_question_examples_ko"
    );

    private static final Set<String> SOURCE_REF_ALLOWED_KEYS = Set.of(
        "source_id",
        "source_title",
        "source_license"
    );

    private final ObjectMapper objectMapper;

    public AiRagContextItem toRagContextItem(CorpusSearchResult result) {
        return new AiRagContextItem(
            "global_corpus",
            result.recordId(),
            result.recordType(),
            result.target(),
            result.difficulty(),
            result.followupStrategy(),
            readStringList(result.topicPathJson()),
            normalizeScore(result.score()),
            buildText(result),
            filterMap(readObjectMap(result.rubricJson()), RUBRIC_ALLOWED_KEYS),
            filterMap(readObjectMap(result.followupPatternExtJson()), FOLLOWUP_PATTERN_EXT_ALLOWED_KEYS),
            readObjectMapList(result.sourceRefsJson()).stream()
                .map(sourceRef -> filterMap(sourceRef, SOURCE_REF_ALLOWED_KEYS))
                .filter(sourceRef -> !sourceRef.isEmpty())
                .toList()
        );
    }

    private Double normalizeScore(double score) {
        if (Double.isNaN(score) || Double.isInfinite(score)) {
            return null;
        }

        return Math.max(0.0, Math.min(1.0, score));
    }

    private Map<String, Object> filterMap(Map<String, Object> source, Set<String> allowedKeys) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }

        Map<String, Object> filtered = new LinkedHashMap<>();

        for (String key : allowedKeys) {
            if (source.containsKey(key)) {
                filtered.put(key, source.get(key));
            }
        }

        return filtered;
    }

    private String buildText(CorpusSearchResult result) {
        if (result.conceptKo() != null && !result.conceptKo().isBlank()) {
            return result.conceptKo();
        }

        if (result.interviewUse() != null && !result.interviewUse().isBlank()) {
            return result.interviewUse();
        }

        if (result.followupQuestionKo() != null && !result.followupQuestionKo().isBlank()) {
            return result.followupQuestionKo();
        }

        if (result.questionKo() != null && !result.questionKo().isBlank()) {
            return result.questionKo();
        }

        if (result.embeddingTextKo() != null && !result.embeddingTextKo().isBlank()) {
            return result.embeddingTextKo();
        }

        return "";
    }

    private List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception exception) {
            return List.of();
        }
    }

    private Map<String, Object> readObjectMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private List<Map<String, Object>> readObjectMapList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception exception) {
            return List.of();
        }
    }
}
