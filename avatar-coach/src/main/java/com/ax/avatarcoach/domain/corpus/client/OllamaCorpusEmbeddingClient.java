package com.ax.avatarcoach.domain.corpus.client;

import com.ax.avatarcoach.domain.corpus.config.CorpusEmbeddingProperties;
import com.ax.avatarcoach.domain.corpus.service.CorpusEmbeddingClient;
import com.ax.avatarcoach.domain.corpus.service.CorpusEmbeddingVectorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "rag.embedding", name = "provider", havingValue = "ollama")
public class OllamaCorpusEmbeddingClient implements CorpusEmbeddingClient {

    private final CorpusEmbeddingProperties properties;
    private final CorpusEmbeddingVectorValidator vectorValidator;

    @Override
    public List<Double> embed(String text) {
        RestClient restClient = RestClient.builder()
            .baseUrl(properties.baseUrl())
            .build();

        OllamaEmbedRequest request = new OllamaEmbedRequest(
            properties.model(),
            List.of(text == null ? "" : text)
        );

        OllamaEmbedResponse response = restClient.post()
            .uri("/api/embed")
            .body(request)
            .retrieve()
            .body(OllamaEmbedResponse.class);

        if (response == null) {
            throw new IllegalStateException("Ollama embedding response is empty");
        }

        return vectorValidator.validateAndNormalize(
            response.firstEmbedding(),
            properties.dimensions()
        );
    }
}
