package com.ax.avatarcoach.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gcp.storage")
public record GcpStorageProperties(
    boolean enabled,
    String bucketName,
    String documentPrefix,
    long signedUrlExpirationMinutes
) {
}
