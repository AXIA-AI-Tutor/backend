package com.ax.avatarcoach.global.ai.client;

import com.ax.avatarcoach.global.ai.client.dto.*;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Spring Boot 서버에서 FastAPI AI 서버를 호출하는 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayClient {

    private final RestClient aiRestClient;
    private final ObjectMapper objectMapper;

    public String healthCheck() {
        try {
            return aiRestClient.get()
                .uri("/health")
                .retrieve()
                .body(String.class);
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    public AiQuestionGenerateResponse generateQuestion(AiQuestionGenerateRequest request) {
        try {
            return aiRestClient.post()
                .uri("/api/ai/questions")
                .body(request)
                .retrieve()
                .body(AiQuestionGenerateResponse.class);
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    public AiTurnResponse evaluateTurn(AiTurnRequest request) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("user_id", request.userId());
        body.add("session_id", request.sessionId());
        body.add("answer_id", request.answerId());
        body.add("mode", request.mode());
        body.add("question_text", request.questionText());
        if (request.transcript() != null && !request.transcript().isBlank()) {
            body.add("transcript", request.transcript());
        }
        addFileIfPresent(body, "file", request.file());
        body.add("vision_metrics", request.visionMetrics());
        addRagContextIfPresent(body, request);

        log.info(
            "Calling AI evaluateTurn. userId={}, sessionId={}, answerId={}, transcriptPresent={}, fileNull={}, fileEmpty={}, fileSize={}, fileContentType={}, fileName={}",
            request.userId(),
            request.sessionId(),
            request.answerId(),
            request.transcript() != null && !request.transcript().isBlank(),
            request.file() == null,
            request.file() != null && request.file().isEmpty(),
            request.file() != null ? request.file().getSize() : null,
            request.file() != null ? request.file().getContentType() : null,
            request.file() != null ? request.file().getOriginalFilename() : null
        );

        try {
            return aiRestClient.post()
                .uri("/api/ai/turn")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(AiTurnResponse.class);
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    private void addFileIfPresent(MultiValueMap<String, Object> body, String partName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        try {
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "answer.webm";
            String contentType = file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            HttpHeaders partHeaders = new HttpHeaders();
            partHeaders.setContentType(MediaType.parseMediaType(contentType));
            partHeaders.setContentDisposition(
                ContentDisposition.builder("form-data")
                    .name(partName)
                    .filename(fileName)
                    .build()
            );

            body.add(partName, new HttpEntity<>(fileResource, partHeaders));
        } catch (IOException exception) {
            log.warn("Failed to read multipart file for AI request. partName={}, fileName={}", partName, file.getOriginalFilename(), exception);
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private void addRagContextIfPresent(
        MultiValueMap<String, Object> body,
        AiTurnRequest request
    ) {
        if (request.ragContext() == null || request.ragContext().isEmpty()) {
            return;
        }

        try {
            body.add("rag_context", objectMapper.writeValueAsString(request.ragContext()));
        } catch (JsonProcessingException exception) {
            log.warn("Failed to serialize turn rag_context. answerId={}", request.answerId(), exception);
        }
    }

    public AiSttResponse transcribe(MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("is_final", true);

        try {
            return aiRestClient.post()
                .uri("/api/ai/stt")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(AiSttResponse.class);
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    public AiReportGenerateResponse generateReport(AiReportGenerateRequest request) {
        try {
            return aiRestClient.post()
                .uri("/api/ai/reports")
                .body(request)
                .retrieve()
                .body(AiReportGenerateResponse.class);
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    public void warmup(AiWarmupRequest request) {
        try {
            aiRestClient.post()
                .uri("/api/ai/warmup")
                .body(request)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    public AiEmbeddingResponse generateEmbedding(AiEmbeddingRequest request) {
        try {
            return aiRestClient.post()
                .uri("/api/ai/embeddings")
                .body(request)
                .retrieve()
                .body(AiEmbeddingResponse.class);
        } catch (RestClientException exception) {
            throw toAiGatewayException(exception);
        }
    }

    private CustomException toAiGatewayException(RestClientException exception) {
        if (exception instanceof ResourceAccessException) {
            log.warn("AI server connection failed", exception);
            return new CustomException(ErrorCode.AI_SERVER_UNAVAILABLE);
        }

        if (exception instanceof HttpStatusCodeException statusException) {
            log.warn(
                "AI server returned error. status={}, body={}",
                statusException.getStatusCode(),
                statusException.getResponseBodyAsString(),
                statusException
            );
            return new CustomException(ErrorCode.AI_SERVER_ERROR);
        }

        log.warn("AI server request failed", exception);
        return new CustomException(ErrorCode.AI_SERVER_ERROR);
    }
}
