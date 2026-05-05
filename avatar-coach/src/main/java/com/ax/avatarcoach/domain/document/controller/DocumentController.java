package com.ax.avatarcoach.domain.document.controller;

import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlRequest;
import com.ax.avatarcoach.domain.document.dto.DocumentUploadUrlResponse;
import com.ax.avatarcoach.domain.document.service.DocumentService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Document", description = "문서 API")
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "업로드 URL 발급", description = "현재 로그인 사용자의 문서 업로드용 GCS Signed URL을 발급합니다.")
    @PostMapping("/upload-url")
    public ApiResponse<DocumentUploadUrlResponse> issueUploadUrl(
        @Valid @RequestBody DocumentUploadUrlRequest request,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(documentService.issueUploadUrl(request, oAuth2User));
    }
}
