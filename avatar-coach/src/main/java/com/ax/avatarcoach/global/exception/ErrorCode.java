package com.ax.avatarcoach.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증 정보가 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND", "세션을 찾을 수 없습니다."),
    SESSION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "SESSION_ACCESS_DENIED", "해당 세션에 접근할 수 없습니다."),
    SESSION_DOCUMENT_REQUIRED(HttpStatus.BAD_REQUEST, "SESSION_DOCUMENT_REQUIRED", "세션을 시작하려면 업로드 완료된 문서가 필요합니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "ANSWER_NOT_FOUND", "답변을 찾을 수 없습니다."),
    ANSWER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ANSWER_ACCESS_DENIED", "해당 답변에 접근할 수 없습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "리포트를 찾을 수 없습니다."),
    REPORT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REPORT_ACCESS_DENIED", "해당 리포트에 접근할 수 없습니다."),
    REPORT_ALREADY_EXISTS(HttpStatus.CONFLICT, "REPORT_ALREADY_EXISTS", "이미 생성된 리포트가 있습니다."),
    REPORT_ANSWER_REQUIRED(HttpStatus.BAD_REQUEST, "REPORT_ANSWER_REQUIRED", "리포트를 생성하려면 답변이 1개 이상 필요합니다."),
    SESSION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "SESSION_NOT_COMPLETED", "완료된 세션만 리포트를 생성할 수 있습니다."),
    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "문서를 찾을 수 없습니다."),
    DOCUMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DOCUMENT_ACCESS_DENIED", "해당 문서에 접근할 수 없습니다."),
    DOCUMENT_ALREADY_UPLOADED(HttpStatus.CONFLICT, "DOCUMENT_ALREADY_UPLOADED", "이미 업로드 완료된 문서입니다."),
    INVALID_UPLOAD_STATUS(HttpStatus.CONFLICT, "INVALID_UPLOAD_STATUS", "업로드 완료 가능한 상태가 아닙니다."),
    STORAGE_DISABLED(HttpStatus.SERVICE_UNAVAILABLE, "STORAGE_DISABLED", "스토리지 기능이 비활성화되어 있습니다. 설정을 확인해주세요."),
    STORAGE_OBJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "STORAGE_OBJECT_NOT_FOUND", "스토리지에 업로드된 파일을 찾을 수 없습니다."),
    STORAGE_FILE_SIZE_MISMATCH(HttpStatus.BAD_REQUEST, "STORAGE_FILE_SIZE_MISMATCH", "업로드 파일 크기가 요청 정보와 일치하지 않습니다."),
    STORAGE_CONTENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "STORAGE_CONTENT_TYPE_MISMATCH", "업로드 파일 타입이 요청 정보와 일치하지 않습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "INVALID_FILE_NAME", "원본 파일명이 유효하지 않습니다."),
    INVALID_FILE_SIZE(HttpStatus.BAD_REQUEST, "INVALID_FILE_SIZE", "파일 크기는 0보다 커야 합니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_SIZE_EXCEEDED", "파일 크기 제한(10MB)을 초과했습니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "UNSUPPORTED_FILE_TYPE", "허용되지 않은 파일 형식입니다."),
    AI_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AI_SERVER_UNAVAILABLE", "AI 서버에 연결할 수 없습니다."),
    AI_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "AI_SERVER_ERROR", "AI 서버 처리 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
