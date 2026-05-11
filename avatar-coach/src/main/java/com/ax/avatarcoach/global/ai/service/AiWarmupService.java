package com.ax.avatarcoach.global.ai.service;

import com.ax.avatarcoach.global.ai.client.AiGatewayClient;
import com.ax.avatarcoach.global.ai.client.dto.AiWarmupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiWarmupService {

    private final AiGatewayClient aiGatewayClient;

    public void warmupForSessionCreate() {
        CompletableFuture.runAsync(() -> {
            try {
                aiGatewayClient.warmup(AiWarmupRequest.defaultSessionWarmup());
            } catch (Exception exception) {
                log.warn("AI warmup failed. Continue session flow without blocking.", exception);
            }
        });
    }
}
