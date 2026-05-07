package com.ax.avatarcoach.domain.document.controller;

import com.ax.avatarcoach.domain.document.dto.DocumentMetadataResponse;
import com.ax.avatarcoach.domain.document.service.DocumentService;
import com.ax.avatarcoach.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Document", description = "문서 조회 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentQueryController {

    private final DocumentService documentService;

    @Operation(summary = "문서 단건 조회", description = "현재 로그인 사용자의 문서 메타데이터를 조회합니다.")
    @GetMapping("/documents/{documentId}")
    public ApiResponse<DocumentMetadataResponse> getDocument(
        @PathVariable Long documentId,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(documentService.getMyDocument(documentId, oAuth2User));
    }

    @Operation(summary = "세션 문서 목록 조회", description = "현재 로그인 사용자 세션에 연결된 문서 메타데이터 목록을 조회합니다.")
    @GetMapping("/sessions/{sessionId}/documents")
    public ApiResponse<Page<DocumentMetadataResponse>> getSessionDocuments(
        @PathVariable Long sessionId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return ApiResponse.success(documentService.getMySessionDocuments(sessionId, page, size, oAuth2User));
    }
}
