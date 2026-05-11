package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchCondition;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.entity.SessionTarget;
import com.ax.avatarcoach.global.ai.client.dto.AiPlanHints;
import com.ax.avatarcoach.global.ai.client.dto.AiRagContextItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorpusRagContextServiceTest {

    @Mock
    private GlobalCorpusRetrieverService retrieverService;

    @Mock
    private CorpusRagContextMapper ragContextMapper;

    @Mock
    private Session session;

    @Test
    void buildQuestionRagContextReturnsEmptyListWhenRetrieverFails() {
        CorpusRagContextService service = new CorpusRagContextService(
            retrieverService,
            ragContextMapper
        );

        when(session.getId()).thenReturn(1L);
        when(session.getTarget()).thenReturn(SessionTarget.BACKEND);
        when(retrieverService.search(any(CorpusSearchCondition.class)))
            .thenThrow(new RuntimeException("embedding server unavailable"));

        List<AiRagContextItem> result = service.buildQuestionRagContext(
            session,
            "JWT refresh token trade-off"
        );

        assertThat(result).isEmpty();
    }

    @Test
    void buildPlanHintsExtractsQuestionHintsFromRagContext() {
        CorpusRagContextService service = new CorpusRagContextService(
            retrieverService,
            ragContextMapper
        );

        AiRagContextItem item = new AiRagContextItem(
            "global_corpus",
            "BACKEND-followup_pattern-1",
            "followup_pattern",
            "BACKEND",
            "NORMAL",
            "tradeoff",
            List.of("backend", "auth", "jwt"),
            0.91,
            "refresh token 재발급 기준을 확인한다.",
            Map.of("must_include", List.of("판단 기준", "보안 trade-off")),
            Map.of(
                "anchor_examples_ko", List.of("refresh token 만료", "토큰 재발급"),
                "negative_phrases_ko", List.of("좀 더 자세히"),
                "good_question_examples_ko", List.of("refresh token 만료 시간을 어떤 기준으로 정했나요?")
            ),
            List.of()
        );

        AiPlanHints result = service.buildPlanHints(List.of(item));

        assertThat(result).isNotNull();
        assertThat(result.anchorCandidates())
            .containsExactly("refresh token 만료", "토큰 재발급");
        assertThat(result.mustCheck())
            .containsExactly("판단 기준", "보안 trade-off");
        assertThat(result.avoidPhrases())
            .containsExactly("좀 더 자세히");
        assertThat(result.goodQuestionExamples())
            .containsExactly("refresh token 만료 시간을 어떤 기준으로 정했나요?");
    }
}
