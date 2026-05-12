package com.ax.avatarcoach.domain.corpus.job;

import com.ax.avatarcoach.domain.corpus.config.CorpusEmbeddingJobProperties;
import com.ax.avatarcoach.domain.corpus.service.GlobalCorpusEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "corpus.embedding-job", name = "enabled", havingValue = "true")
public class CorpusEmbeddingJobRunner implements ApplicationRunner {

    private final CorpusEmbeddingJobProperties properties;
    private final GlobalCorpusEmbeddingService embeddingService;
    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        int totalEmbeddedCount = 0;

        log.info(
            "Starting corpus embedding job. batchSize={}, maxBatches={}",
            properties.batchSize(),
            properties.maxBatches()
        );

        for (int batchIndex = 1; batchIndex <= properties.maxBatches(); batchIndex++) {
            int embeddedCount = embeddingService.embedPendingRecords(properties.batchSize());
            totalEmbeddedCount += embeddedCount;

            log.info(
                "Completed corpus embedding batch. batchIndex={}, embeddedCount={}, totalEmbeddedCount={}",
                batchIndex,
                embeddedCount,
                totalEmbeddedCount
            );

            if (embeddedCount < properties.batchSize()) {
                log.info("No more pending corpus records to embed. batchIndex={}", batchIndex);
                break;
            }
        }

        log.info("Completed corpus embedding job. totalEmbeddedCount={}", totalEmbeddedCount);

        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }
}
