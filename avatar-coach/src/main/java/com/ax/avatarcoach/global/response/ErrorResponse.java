package com.ax.avatarcoach.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private final boolean success;
    private final String errorCode;
    private final String message;

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(false, errorCode, message);
    }
}
