package com.ax.avatarcoach.domain.document.storage;

import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(prefix = "gcp.storage", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledStorageService implements StorageService {

    @Override
    public SignedUploadUrl generatePutSignedUrl(String bucketName, String objectPath, String contentType, LocalDateTime expiresAt) {
        throw new CustomException(ErrorCode.STORAGE_DISABLED);
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectPath) {
        throw new CustomException(ErrorCode.STORAGE_DISABLED);
    }
}
