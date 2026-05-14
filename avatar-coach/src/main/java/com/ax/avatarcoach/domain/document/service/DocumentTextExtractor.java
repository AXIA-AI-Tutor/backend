package com.ax.avatarcoach.domain.document.service;

import com.ax.avatarcoach.global.exception.CustomException;
import com.ax.avatarcoach.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
@Slf4j
public class DocumentTextExtractor {

    public String extractText(byte[] fileBytes, String fileType, String originalFileName) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new CustomException(ErrorCode.DOCUMENT_TEXT_EMPTY);
        }

        try {
            if (isPdf(fileType, originalFileName)) {
                return extractPdfText(fileBytes);
            }
            if (isDocx(fileType, originalFileName)) {
                return extractDocxText(fileBytes);
            }
            if (isTextPlain(fileType, originalFileName)) {
                return new String(fileBytes, StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            log.warn("[DOCUMENT_TEXT_EXTRACT_FAILED] fileType={}, originalFileName={}, errorType={}", fileType, originalFileName, exception.getClass().getSimpleName());
            throw new CustomException(ErrorCode.DOCUMENT_TEXT_EXTRACT_FAILED);
        }

        throw new CustomException(ErrorCode.DOCUMENT_TEXT_EXTRACT_FAILED);
    }

    private String extractPdfText(byte[] fileBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractDocxText(byte[] fileBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private boolean isPdf(String fileType, String originalFileName) {
        return "application/pdf".equalsIgnoreCase(fileType) || hasExtension(originalFileName, ".pdf");
    }

    private boolean isDocx(String fileType, String originalFileName) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(fileType)
            || hasExtension(originalFileName, ".docx");
    }

    private boolean isTextPlain(String fileType, String originalFileName) {
        return "text/plain".equalsIgnoreCase(fileType) || hasExtension(originalFileName, ".txt");
    }

    private boolean hasExtension(String fileName, String extension) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        return fileName.toLowerCase(Locale.ROOT).endsWith(extension);
    }
}
