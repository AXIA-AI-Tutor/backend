package com.ax.avatarcoach.global.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Registers the RestClient used for AI gateway calls.
 */
@Configuration
@EnableConfigurationProperties(AiGatewayProperties.class)
public class AiGatewayConfig {

    @Bean
    public RestClient aiRestClient(AiGatewayProperties properties) {
        RestClient.Builder builder = RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(new SimpleClientHttpRequestFactory());

        if (properties.token() != null && !properties.token().isBlank()) {
            builder.defaultHeader("X-Internal-Token", properties.token());
        }

        return builder.build();
    }
}
