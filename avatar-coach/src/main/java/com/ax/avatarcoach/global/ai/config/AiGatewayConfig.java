package com.ax.avatarcoach.global.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * AI 서버 호출에 사용할 RestClient를 Spring Bean으로 등록하는 설정 클래스
 * AiGatewayProperties의 baseUrl을 기본 주소로 사용
 */
@Configuration
@EnableConfigurationProperties(AiGatewayProperties.class)
public class AiGatewayConfig {

    @Bean
    public RestClient aiRestClient(AiGatewayProperties properties) {
        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .build();
    }
}
