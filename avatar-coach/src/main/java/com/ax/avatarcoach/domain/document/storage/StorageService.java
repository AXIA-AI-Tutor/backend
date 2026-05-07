package com.ax.avatarcoach.domain.document.storage;

import java.time.LocalDateTime;

public interface StorageService {

    SignedUploadUrl generatePutSignedUrl(String bucketName, String objectPath, String contentType, LocalDateTime expiresAt);

    ObjectMetadata getObjectMetadata(String bucketName, String objectPath);

    record SignedUploadUrl(String uploadUrl) {
    }

    record ObjectMetadata(long size, String contentType) {
    }
}
