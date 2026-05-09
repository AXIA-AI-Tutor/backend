package com.ax.avatarcoach.global.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration values for calling the AI gateway.
 */
@ConfigurationProperties(prefix = "ai.server")
public record AiGatewayProperties(
    String baseUrl,
    String token
) {
}
