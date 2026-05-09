package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchResult;
import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CorpusRagContextMapper {

    private final ObjectMapper objectMapper;

    public AiQuestionGenerateRequest.RagContextItem toRagContextItem(CorpusSearchResult result) {
        return new AiQuestionGenerateRequest.RagContextItem(
            "global_corpus",
            result.recordId(),
            result.recordType(),
            result.target(),
            result.difficulty(),
            result.followupStrategy(),
            readStringList(result.topicPathJson()),
            result.score(),
            buildText(result),
            readObjectMap(result.rubricJson()),
            readObjectMap(result.followupPatternExtJson()),
            readObjectMapList(result.sourceRefsJson())
        );
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
