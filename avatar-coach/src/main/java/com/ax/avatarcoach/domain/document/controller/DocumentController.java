package com.ax.avatarcoach.domain.document.controller;

import com.ax.avatarcoach.domain.document.dto.DocumentMetadataCreateRequest;
import com.ax.avatarcoach.domain.document.dto.DocumentMetadataResponse;
import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlRequest;
import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlResponse;
import com.ax.avatarcoach.domain.document.dto.DocumentResponse;
import com.ax.avatarcoach.domain.document.service.DocumentService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Document", description = "문서 API")
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "문서 메타데이터 저장", description = "현재 로그인 사용자의 문서 메타데이터를 저장합니다.")
    @PostMapping("/metadata")
    public ApiResponse<DocumentMetadataResponse> createMetadata(
        @Valid @RequestBody DocumentMetadataCreateRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(documentService.createMetadata(request, oAuth2User));
    }

    @Operation(summary = "업로드 URL 발급", description = "현재 로그인 사용자의 문서 업로드용 GCS Signed URL을 발급합니다.")
    @PostMapping("/upload-url")
    public ApiResponse<DocumentUploadUrlResponse> issueUploadUrl(
        @Valid @RequestBody DocumentUploadUrlRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(documentService.issueUploadUrl(request, oAuth2User));
    }

    @Operation(summary = "업로드 완료 처리", description = "현재 로그인 사용자의 문서 업로드 완료를 검증하고 상태를 갱신합니다.")
    @PostMapping("/{documentId}/complete")
    public ApiResponse<DocumentResponse> completeUpload(
        @PathVariable Long documentId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(DocumentResponse.from(documentService.completeUpload(documentId, oAuth2User)));
    }

    @Operation(summary = "문서 요약 생성", description = "현재 로그인 사용자의 문서 요약을 AI Gateway로 생성하고 저장합니다.")
    @PostMapping("/{documentId}/summary")
    public ApiResponse<DocumentResponse> generateSummary(
        @PathVariable Long documentId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(DocumentResponse.from(documentService.generateSummary(documentId, oAuth2User)));
    }
}
