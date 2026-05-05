package com.ax.avatarcoach.global.ai.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Spring Boot 서버에서 FastAPI AI 서버를 호출하는 클라이언트
 * 실제 질문/피드백/리포트 API 호출 메서드는 이후 작업에서 추가
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
}
