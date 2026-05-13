package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchResult;
import com.ax.avatarcoach.global.ai.client.dto.AiRagContextItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorpusRagContextMapperTest {

    private final CorpusRagContextMapper mapper = new CorpusRagContextMapper(new ObjectMapper());

    @Test
    void toRagContextItemKeepsOnlyAiContractFields() {
        CorpusSearchResult result = new CorpusSearchResult(
            1L,
            "BACKEND-followup_pattern-1",
            "BACKEND",
            "followup_pattern",
            "NORMAL",
            "tradeoff",
            "[\"backend\",\"auth\"]",
            "[]",
            "embedding text",
            "question",
            "answer",
            "followup",
            "concept",
            "interview use",
            "{\"must_include\":[\"security\"],\"bad_signals\":[\"always safe\"],\"good_signals\":[\"trade-off\"]}",
            "{\"anchor_examples_ko\":[\"refresh token\"],\"negative_phrases_ko\":[\"tell me more\"],\"anchor_kind\":\"term\"}",
            "[{\"source_id\":\"owasp_asvs\",\"source_title\":\"ASVS\",\"source_license\":\"CC-BY-SA-4.0\",\"source_chunk_id\":\"chunk-1\",\"source_url\":\"https://example.com\"}]",
            1.2,
            -0.2
        );

        AiRagContextItem item = mapper.toRagContextItem(result);

        assertThat(item.score()).isEqualTo(0.0);
        assertThat(item.rubric()).containsOnlyKeys("must_include", "bad_signals");
        assertThat(item.followupPatternExt()).containsOnlyKeys(
            "anchor_examples_ko",
            "negative_phrases_ko"
        );
        assertThat(item.sourceRefs()).hasSize(1);
        assertThat(item.sourceRefs().get(0)).containsOnlyKeys(
            "source_id",
            "source_title",
            "source_license"
        );
    }
}
