package com.ax.avatarcoach.domain.document.service;

import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentTextExtractorTest {

    private final DocumentTextExtractor extractor = new DocumentTextExtractor();

    @Test
    void extractText_fromTxt_returnsUtf8String() {
        byte[] bytes = "안녕하세요 avatar coach".getBytes(StandardCharsets.UTF_8);

        String text = extractor.extractText(bytes, "text/plain", "resume.txt");

        assertEquals("안녕하세요 avatar coach", text);
    }

    @Test
    void extractText_withUnsupportedExtension_throwsException() {
        CustomException exception = assertThrows(CustomException.class,
            () -> extractor.extractText("data".getBytes(StandardCharsets.UTF_8), "application/zip", "archive.zip"));

        assertEquals(ErrorCode.DOCUMENT_TEXT_EXTRACT_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("문서 텍스트 추출"));
    }
}
