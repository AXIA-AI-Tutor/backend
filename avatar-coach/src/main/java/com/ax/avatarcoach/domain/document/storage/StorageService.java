package com.ax.avatarcoach.domain.document.storage;

import java.time.LocalDateTime;

public interface StorageService {

    SignedUploadUrl generatePutSignedUrl(String bucketName, String objectPath, String contentType, LocalDateTime expiresAt);

    record SignedUploadUrl(String uploadUrl) {
    }
}
