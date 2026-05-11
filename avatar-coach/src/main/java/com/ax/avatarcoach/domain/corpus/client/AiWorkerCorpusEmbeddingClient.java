package com.ax.avatarcoach.domain.corpus.client;

import com.ax.avatarcoach.domain.corpus.config.CorpusEmbeddingProperties;
import com.ax.avatarcoach.domain.corpus.service.CorpusEmbeddingClient;
import com.ax.avatarcoach.domain.corpus.service.CorpusEmbeddingVectorValidator;
import com.ax.avatarcoach.global.ai.client.AiGatewayClient;
import com.ax.avatarcoach.global.ai.client.dto.AiEmbeddingRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiEmbeddingResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "rag.embedding", name = "provider", havingValue = "ai_worker")
public class AiWorkerCorpusEmbeddingClient implements CorpusEmbeddingClient {

    private final AiGatewayClient aiGatewayClient;
    private final CorpusEmbeddingProperties properties;
    private final CorpusEmbeddingVectorValidator vectorValidator;

    @Override
    public List<Double> embed(String text) {
        AiEmbeddingResponse response = aiGatewayClient.generateEmbedding(
            AiEmbeddingRequest.query(
                "corpus-query-" + UUID.randomUUID(),
                text == null ? "" : text
            )
        );

        if (response == null) {
            throw new IllegalStateException("AI embedding response is empty");
        }

        if (response.dimensions() != properties.dimensions()) {
            throw new IllegalStateException(
                "AI embedding dimension mismatch. expected="
                    + properties.dimensions()
                    + ", actual="
                    + response.dimensions()
            );
        }

        if (!response.normalized()) {
            throw new IllegalStateException("AI embedding response must be normalized");
        }

        return vectorValidator.validateAndNormalize(
            response.firstEmbedding(),
            properties.dimensions()
        );
    }
}
