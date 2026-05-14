package com.ax.avatarcoach.domain.document.service;

import com.ax.avatarcoach.domain.document.dto.DocumentMetadataCreateRequest;
import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlRequest;
import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlResponse;
import com.ax.avatarcoach.domain.document.dto.DocumentMetadataResponse;
import com.ax.avatarcoach.domain.document.entity.Document;
import com.ax.avatarcoach.domain.document.entity.DocumentStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
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
    private final DocumentTextExtractor documentTextExtractor;
    private final GcpStorageProperties gcpStorageProperties;

    @Transactional
    public DocumentMetadataResponse createMetadata(DocumentMetadataCreateRequest request, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        Session session = sessionRepository.findByIdAndUser(request.sessionId(), user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateMetadata(request.originalFileName(), request.fileSize(), request.fileType());

        String sanitizedFileName = sanitizeFileName(request.originalFileName());
        String storagePath = buildStoragePath(user.getId(), session.getId(), sanitizedFileName);

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
            null
        );

        Document savedDocument = documentRepository.save(document);
        log.info(
            "[DOCUMENT_METADATA_CREATED] sessionId={}, userId={}, documentId={}, docType={}, originalFileName={}, fileSize={}, uploadStatus={}, status={}, storageBucket={}, storagePath={}, createdAt={}",
            savedDocument.getSession().getId(),
            savedDocument.getUser().getId(),
            savedDocument.getId(),
            savedDocument.getDocType(),
            savedDocument.getOriginalFileName(),
            savedDocument.getFileSize(),
            savedDocument.getUploadStatus(),
            savedDocument.getStatus(),
            savedDocument.getStorageBucket(),
            savedDocument.getStoragePath(),
            savedDocument.getCreatedAt()
        );
        return DocumentMetadataResponse.from(savedDocument);
    }

    @Transactional
    public DocumentUploadUrlResponse issueUploadUrl(DocumentUploadUrlRequest request, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        Session session = sessionRepository.findByIdAndUser(request.sessionId(), user)
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        validateMetadata(request.originalFileName(), request.fileSize(), request.fileType());

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
        log.info(
            "[DOCUMENT_METADATA_CREATED] sessionId={}, userId={}, documentId={}, docType={}, originalFileName={}, fileSize={}, uploadStatus={}, status={}, storageBucket={}, storagePath={}, createdAt={}",
            saved.getSession().getId(),
            saved.getUser().getId(),
            saved.getId(),
            saved.getDocType(),
            saved.getOriginalFileName(),
            saved.getFileSize(),
            saved.getUploadStatus(),
            saved.getStatus(),
            saved.getStorageBucket(),
            saved.getStoragePath(),
            saved.getCreatedAt()
        );
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
        log.info(
            "[DOCUMENT_UPLOAD_COMPLETED] documentId={}, sessionId={}, userId={}, uploadStatus={}, status={}, fileSize={}, storagePath={}, uploadedAt={}",
            document.getId(),
            document.getSession().getId(),
            document.getUser().getId(),
            document.getUploadStatus(),
            document.getStatus(),
            document.getFileSize(),
            document.getStoragePath(),
            document.getUploadedAt()
        );
        return document;
    }

    @Transactional
    public Document generateSummary(Long documentId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (!document.isOwnedBy(user)) {
            throw new CustomException(ErrorCode.DOCUMENT_ACCESS_DENIED);
        }

        if (document.getSummary() != null && !document.getSummary().isBlank()) {
            throw new CustomException(ErrorCode.DOCUMENT_SUMMARY_ALREADY_EXISTS);
        }
        if (document.getUploadStatus() != UploadStatus.UPLOADED || document.getStatus() != DocumentStatus.READY_FOR_AI) {
            throw new CustomException(ErrorCode.DOCUMENT_SUMMARY_NOT_ALLOWED);
        }

        DocumentStatus beforeStatus = document.getStatus();
        boolean hasStorageBucket = document.getStorageBucket() != null && !document.getStorageBucket().isBlank();
        boolean hasStoragePath = document.getStoragePath() != null && !document.getStoragePath().isBlank();
        if (!hasStorageBucket || !hasStoragePath) {
            throw new CustomException(ErrorCode.DOCUMENT_FILE_NOT_FOUND);
        }

        log.info(
            "[DOCUMENT_TEXT_EXTRACT_REQUEST] documentId={}, sessionId={}, userId={}, docType={}, fileType={}, originalFileName={}, hasStorageBucket={}, hasStoragePath={}",
            document.getId(),
            document.getSession().getId(),
            document.getUser().getId(),
            document.getDocType(),
            document.getFileType(),
            document.getOriginalFileName(),
            hasStorageBucket,
            hasStoragePath
        );

        document.markProcessing();
        try {
            byte[] fileBytes = storageService.downloadObject(document.getStorageBucket(), document.getStoragePath());
            String extractedText = documentTextExtractor.extractText(fileBytes, document.getFileType(), document.getOriginalFileName());
            if (extractedText == null || extractedText.isBlank()) {
                throw new CustomException(ErrorCode.DOCUMENT_TEXT_EMPTY);
            }
            log.info("[DOCUMENT_TEXT_EXTRACTED] documentId={}, extractedTextLength={}, fileType={}",
                document.getId(), extractedText.length(), document.getFileType());

            document.completeSummary(extractedText);
            log.info(
                "[DOCUMENT_SUMMARY_SAVED] documentId={}, sessionId={}, userId={}, summaryLength={}, status={}",
                document.getId(),
                document.getSession().getId(),
                document.getUser().getId(),
                extractedText.length(),
                document.getStatus()
            );
            return document;
        } catch (RuntimeException exception) {
            if (document.getStatus() == DocumentStatus.PROCESSING) {
                document.markSummaryFailed();
            }
            log.warn(
                "[DOCUMENT_SUMMARY_FAILED] documentId={}, exceptionClass={}, exceptionMessage={}, status={}",
                document.getId(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                document.getStatus()
            );
            throw exception;
        }
    }


    public DocumentMetadataResponse getMyDocument(Long documentId, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new CustomException(ErrorCode.DOCUMENT_NOT_FOUND));

        if (!document.isOwnedBy(user)) {
            throw new CustomException(ErrorCode.DOCUMENT_ACCESS_DENIED);
        }

        return DocumentMetadataResponse.from(document);
    }

    public Page<DocumentMetadataResponse> getMySessionDocuments(Long sessionId, int page, int size, OAuth2User oAuth2User) {
        User user = getCurrentUser(oAuth2User);
        sessionRepository.findByIdAndUserId(sessionId, user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.SESSION_ACCESS_DENIED));

        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findAllBySessionIdAndUserIdOrderByCreatedAtDesc(sessionId, user.getId(), pageable)
            .map(DocumentMetadataResponse::from);
    }

    private void validateMetadata(String originalFileName, Long fileSize, String fileType) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }
        if (fileSize == null || fileSize <= 0) {
            throw new CustomException(ErrorCode.INVALID_FILE_SIZE);
        }
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
        if (fileType == null || !ALLOWED_FILE_TYPES.contains(fileType)) {
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
            .replaceAll("\\.{2,}", ".")
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
