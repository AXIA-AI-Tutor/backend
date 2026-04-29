package com.ax.avatarcoach.global.exception;

import com.ax.avatarcoach.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전체 컨트롤러의 예외를 공통 처리
public class GlobalExceptionHandler {

    // CustomException 발생 시 실행
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode.getCode(), errorCode.getMessage()));
    }

    // 예상 못한 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode.getCode(), errorCode.getMessage()));
    }
}
