package com.ax.avatarcoach.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
@Component
@Slf4j
@RequiredArgsConstructor
public class AppConfigStartupLogger implements ApplicationRunner {

    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        String[] profiles = environment.getActiveProfiles();
        String profile = profiles.length == 0 ? "default" : String.join(",", profiles);
        String datasourceUrl = environment.getProperty("spring.datasource.url", "");

        String datasourceHost = "unknown";
        String database = "unknown";

        if (datasourceUrl != null && datasourceUrl.startsWith("jdbc:")) {
            String sanitizedUrl = datasourceUrl.substring("jdbc:".length());
            try {
                URI uri = URI.create(sanitizedUrl);
                datasourceHost = uri.getHost() != null ? uri.getHost() : "unknown";
                String path = uri.getPath();
                if (path != null && path.length() > 1) {
                    database = path.substring(1);
                }
            } catch (IllegalArgumentException ignored) {
                log.warn("[APP_CONFIG] failed to parse datasource url");
            }
        }

        log.info("[APP_CONFIG] profile={}, datasourceHost={}, database={}", profile, datasourceHost, database);
    }
}
