package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchCondition;
import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchResult;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.global.ai.client.dto.AiPlanHints;
import com.ax.avatarcoach.global.ai.client.dto.AiRagContextItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorpusRagContextService {

    private static final int RAG_CONTEXT_LIMIT = 5;
    private static final int MAX_ANCHOR_CANDIDATES = 6;
    private static final int MAX_MUST_CHECK = 4;
    private static final int MAX_AVOID_PHRASES = 4;
    private static final int MAX_GOOD_QUESTION_EXAMPLES = 2;

    private final GlobalCorpusRetrieverService retrieverService;
    private final CorpusRagContextMapper ragContextMapper;

    public List<AiRagContextItem> buildQuestionRagContext(
        Session session,
        String query
    ) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        try {
            List<CorpusSearchResult> results = retrieverService.search(new CorpusSearchCondition(
                query,
                session.getTarget().name(),
                List.of("followup_pattern", "interview_rubric", "tutoring_qa"),
                null,
                RAG_CONTEXT_LIMIT
            ));

            return results.stream()
                .map(ragContextMapper::toRagContextItem)
                .toList();
        } catch (Exception exception) {
            log.warn("Failed to build question rag_context. sessionId={}", session.getId(), exception);
            return List.of();
        }
    }

    public AiPlanHints buildPlanHints(List<AiRagContextItem> ragContext) {
        if (ragContext == null || ragContext.isEmpty()) {
            return null;
        }

        AiPlanHints planHints = new AiPlanHints(
            collectStringValues(ragContext, "anchor_examples_ko", MAX_ANCHOR_CANDIDATES),
            collectRubricValues(ragContext, "must_include", MAX_MUST_CHECK),
            collectStringValues(ragContext, "negative_phrases_ko", MAX_AVOID_PHRASES),
            collectStringValues(ragContext, "good_question_examples_ko", MAX_GOOD_QUESTION_EXAMPLES)
        );

        return planHints.isEmpty() ? null : planHints;
    }

    public List<AiRagContextItem> buildTurnRagContext(
        Session session,
        String questionText,
        String transcript
    ) {
        String query = buildTurnRagQuery(questionText, transcript);

        if (query.isBlank()) {
            return List.of();
        }

        try {
            List<CorpusSearchResult> results = retrieverService.search(new CorpusSearchCondition(
                query,
                session.getTarget().name(),
                List.of("concept_card", "interview_rubric"),
                null,
                RAG_CONTEXT_LIMIT
            ));

            return results.stream()
                .map(ragContextMapper::toRagContextItem)
                .toList();
        } catch (Exception exception) {
            log.warn("Failed to build turn rag_context. sessionId={}", session.getId(), exception);
            return List.of();
        }
    }

    private String buildTurnRagQuery(String questionText, String transcript) {
        String question = questionText == null ? "" : questionText.trim();
        String answer = transcript == null ? "" : transcript.trim();

        if (question.isBlank()) {
            return answer;
        }

        if (answer.isBlank()) {
            return question;
        }

        return question + "\n" + answer;
    }

    private List<String> collectStringValues(
        List<AiRagContextItem> ragContext,
        String key,
        int limit
    ) {
        List<String> values = new ArrayList<>();

        for (AiRagContextItem item : ragContext) {
            Object rawValues = item.followupPatternExt() == null
                ? null
                : item.followupPatternExt().get(key);

            addStringList(values, rawValues, limit);

            if (values.size() >= limit) {
                break;
            }
        }

        return values;
    }

    private List<String> collectRubricValues(
        List<AiRagContextItem> ragContext,
        String key,
        int limit
    ) {
        List<String> values = new ArrayList<>();

        for (AiRagContextItem item : ragContext) {
            Object rawValues = item.rubric() == null
                ? null
                : item.rubric().get(key);

            addStringList(values, rawValues, limit);

            if (values.size() >= limit) {
                break;
            }
        }

        return values;
    }

    private void addStringList(List<String> target, Object rawValues, int limit) {
        if (!(rawValues instanceof List<?> values)) {
            return;
        }

        for (Object value : values) {
            if (target.size() >= limit) {
                return;
            }

            String text = Objects.toString(value, "").trim();

            if (!text.isBlank() && !target.contains(text)) {
                target.add(text);
            }
        }
    }
}
