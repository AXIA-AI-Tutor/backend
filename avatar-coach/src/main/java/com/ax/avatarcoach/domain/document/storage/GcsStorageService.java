package com.ax.avatarcoach.domain.document.storage;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class GcsStorageService implements StorageService {

    private final Storage storage;

    @Override
    public SignedUploadUrl generatePutSignedUrl(String bucketName, String objectPath, String contentType, LocalDateTime expiresAt) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectPath)
            .setContentType(contentType)
            .build();

        long seconds = Duration.between(LocalDateTime.now(ZoneOffset.UTC), expiresAt).getSeconds();
        URL signedUrl = storage.signUrl(
            blobInfo,
            Math.max(seconds, 1L),
            java.util.concurrent.TimeUnit.SECONDS,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withV4Signature(),
            Storage.SignUrlOption.withContentType()
        );

        return new SignedUploadUrl(signedUrl.toString());
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectPath) {
        BlobId blobId = BlobId.of(bucketName, objectPath);
        com.google.cloud.storage.Blob blob = storage.get(blobId);
        if (blob == null || !blob.exists()) {
            return null;
        }
        return new ObjectMetadata(blob.getSize(), blob.getContentType());
    }
}
