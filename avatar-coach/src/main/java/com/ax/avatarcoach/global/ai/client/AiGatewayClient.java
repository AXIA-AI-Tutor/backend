package com.ax.avatarcoach.global.ai.client;

import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiQuestionGenerateResponse;
import com.ax.avatarcoach.global.ai.client.dto.AiReportGenerateRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiReportGenerateResponse;
import com.ax.avatarcoach.global.ai.client.dto.AiTurnRequest;
import com.ax.avatarcoach.global.ai.client.dto.AiTurnResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Spring Boot 서버에서 FastAPI AI 서버를 호출하는 클라이언트
 */
@Component
@RequiredArgsConstructor
public class AiGatewayClient {

    private final RestClient aiRestClient;

    public String healthCheck() {
        return aiRestClient.get()
            .uri("/health")
            .retrieve()
            .body(String.class);
    }

    public AiQuestionGenerateResponse generateQuestion(AiQuestionGenerateRequest request) {
        return aiRestClient.post()
            .uri("/api/ai/questions")
            .body(request)
            .retrieve()
            .body(AiQuestionGenerateResponse.class);
    }

    public AiTurnResponse evaluateTurn(AiTurnRequest request) {
        return aiRestClient.post()
            .uri("/api/ai/turn")
            .body(request)
            .retrieve()
            .body(AiTurnResponse.class);
    }

    public AiReportGenerateResponse generateReport(AiReportGenerateRequest request) {
        return aiRestClient.post()
            .uri("/api/ai/reports")
            .body(request)
            .retrieve()
            .body(AiReportGenerateResponse.class);
    }
}
