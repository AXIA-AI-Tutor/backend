package com.ax.avatarcoach.global.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.internal")
public record InternalApiProperties(
    String apiKey
) {
}
