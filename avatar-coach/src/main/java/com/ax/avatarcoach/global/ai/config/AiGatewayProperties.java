package com.ax.avatarcoach.global.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 서버 연동에 필요한 설정값을 application yaml에서 읽어오는 객체
 * ai.server.base-url 값을 baseUrl 필드로 매핑
 */
@ConfigurationProperties(prefix = "ai.server") // yaml의 ai.server.base-url 값을 Java 객체로 묶어줌
public record AiGatewayProperties(
    String baseUrl // yaml의 base-url과 자동 매핑
) {
}
