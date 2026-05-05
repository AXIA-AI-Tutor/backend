package com.ax.avatarcoach.domain.document.service;

import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlRequest;
import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlResponse;
import com.ax.avatarcoach.domain.document.entity.Document;
import com.ax.avatarcoach.domain.document.entity.StorageProvider;
import com.ax.avatarcoach.domain.document.entity.UploadStatus;
import com.ax.avatarcoach.domain.document.repository.DocumentRepository;
import com.ax.avatarcoach.domain.document.storage.StorageService;
import com.ax.avatarcoach.domain.session.entity.Session;
import com.ax.avatarcoach.domain.session.repository.SessionRepository;
import com.ax.avatarcoach.domain.user.entity.OAuthProvider;
import com.ax.avatarcoach.domain.user.entity.User;
import com.ax.avatarcoach.domain.user.repository.UserRepository;
import com.ax.avatarcoach.global.config.GcpStorageProperties;
import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import com.ax.avatarcoach.global.security.oauth.GoogleOAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
        "application/pdf",
        "text/plain",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final StorageService storageService;
    private final GcpStorageProperties gcpStorageProperties;

    @Transactional
    public DocumentUploadUrlResponse issueUploadUrl(DocumentUploadUrlRequest request, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        Session session = sessionRepository.findByIdAndUser(request.sessionId(), user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateMetadata(request);

        String sanitizedFileName = sanitizeFileName(request.originalFileName());
        String storagePath = buildStoragePath(user.getId(), session.getId(), sanitizedFileName);
        LocalDateTime expiresAt = LocalDateTime.now(ZoneOffset.UTC)
            .plusMinutes(gcpStorageProperties.signedUrlExpirationMinutes());

        Document document = Document.create(
            user,
            session,
            request.docType(),
            request.originalFileName(),
            request.fileType(),
            request.fileSize(),
            StorageProvider.GCS,
            gcpStorageProperties.bucketName(),
            storagePath,
            expiresAt
        );

        Document saved = documentRepository.save(document);
        String uploadUrl = storageService.generatePutSignedUrl(
            gcpStorageProperties.bucketName(),
            storagePath,
            request.fileType(),
            expiresAt
        ).uploadUrl();

        return new DocumentUploadUrlResponse(
            saved.getId(),
            uploadUrl,
            "PUT",
            saved.getStorageProvider(),
            saved.getStorageBucket(),
            saved.getStoragePath(),
            saved.getUploadUrlExpiresAt(),
            Map.of("Content-Type", request.fileType())
        );
    }

    @Transactional
    public Document completeUpload(Long documentId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (!document.isOwnedBy(user)) {
            throw new CustomException(ErrorCode.DOCUMENT_ACCESS_DENIED);
        }

        if (document.getUploadStatus() == UploadStatus.UPLOADED) {
            return document;
        }
        if (document.getUploadStatus() != UploadStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_UPLOAD_STATUS);
        }

        StorageService.ObjectMetadata objectMetadata = storageService.getObjectMetadata(
            document.getStorageBucket(),
            document.getStoragePath()
        );

        if (objectMetadata == null) {
            document.markUploadFailed();
            throw new CustomException(ErrorCode.STORAGE_OBJECT_NOT_FOUND);
        }
        if (!document.getFileSize().equals(objectMetadata.size())) {
            document.markUploadFailed();
            throw new CustomException(ErrorCode.STORAGE_FILE_SIZE_MISMATCH);
        }
        if (!document.getFileType().equals(objectMetadata.contentType())) {
            document.markUploadFailed();
            throw new CustomException(ErrorCode.STORAGE_CONTENT_TYPE_MISMATCH);
        }

        document.markUploaded(LocalDateTime.now(ZoneOffset.UTC));
        return document;
    }

    private void validateMetadata(DocumentUploadUrlRequest request) {
        if (request.originalFileName() == null || request.originalFileName().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }
        if (request.fileSize() == null || request.fileSize() <= 0) {
            throw new CustomException(ErrorCode.INVALID_FILE_SIZE);
        }
        if (request.fileSize() > MAX_FILE_SIZE_BYTES) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
        if (request.fileType() == null || !ALLOWED_FILE_TYPES.contains(request.fileType())) {
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }
    }

    private String buildStoragePath(Long userId, Long sessionId, String fileName) {
        String prefix = gcpStorageProperties.documentPrefix();
        String normalizedPrefix = (prefix == null || prefix.isBlank()) ? "" : prefix.replaceAll("^/+|/+$", "") + "/";
        return normalizedPrefix + "users/" + userId + "/sessions/" + sessionId + "/documents/" + UUID.randomUUID() + "_" + fileName;
    }

    private String sanitizeFileName(String originalFileName) {
        String sanitized = originalFileName.replaceAll("[\\\\/]+", "_")
            .replaceAll("\.\.+", ".")
            .replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitized.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }
        return sanitized;
    }

    private User getCurrentUser(OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        String providerUserId = userInfo.getProviderUserId();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, providerUserId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
