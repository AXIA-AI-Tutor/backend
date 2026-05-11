package com.ax.avatarcoach.global.ai.client;

import com.ax.avatarcoach.global.ai.client.dto.*;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Spring Boot 서버에서 FastAPI AI 서버를 호출하는 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGatewayClient {

    private final RestClient aiRestClient;

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

    public AiTurnResponse evaluateTurn(AiTurnRequest request, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("user_id", request.userId());
        body.add("session_id", request.sessionId());
        body.add("answer_id", request.answerId());
        body.add("mode", request.mode());
        body.add("question_text", request.questionText());
        body.add("vision_metrics", request.visionMetrics());
        body.add("file", file.getResource());

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
