package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchCondition;
import com.ax.avatarcoach.domain.corpus.dto.CorpusSearchResult;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Profile("local")
@Service
@RequiredArgsConstructor
public class CorpusRagContextService {

    private static final int RAG_CONTEXT_LIMIT = 5;

    private final GlobalCorpusRetrieverService retrieverService;
    private final CorpusRagContextMapper ragContextMapper;

    public List<AiQuestionGenerateRequest.RagContextItem> buildQuestionRagContext(
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
}
