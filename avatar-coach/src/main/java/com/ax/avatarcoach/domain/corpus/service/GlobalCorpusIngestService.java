package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.entity.*;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusRecordRepository;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusSourceChunkRepository;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusSourceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class GlobalCorpusIngestService {

    private static final String DEFAULT_LANGUAGE = "ko";

    private final GlobalCorpusSourceRepository sourceRepository;
    private final GlobalCorpusRecordRepository recordRepository;
    private final GlobalCorpusSourceChunkRepository sourceChunkRepository;
    private final ObjectMapper objectMapper;

    public CorpusIngestResult ingestTarget(
        Path targetDirectory,
        String artifactName,
        String artifactVersion,
        String artifactVariant,
        String artifactUri
    ) {
        Path sourceChunksPath = targetDirectory.resolve("source_chunks.jsonl");
        Path recordsPath = targetDirectory.resolve("records.jsonl");

        int sourceChunksSaved = 0;
        int sourceChunksSkipped = 0;
        int recordsSaved = 0;
        int recordsSkipped = 0;

        for (CorpusSourceChunkLine line : readJsonLines(sourceChunksPath, CorpusSourceChunkLine.class)) {
            if (saveSourceChunk(line, artifactName, artifactVersion, artifactVariant, artifactUri)) {
                sourceChunksSaved++;
            } else {
                sourceChunksSkipped++;
            }
        }

        for (CorpusRecordLine line : readJsonLines(recordsPath, CorpusRecordLine.class)) {
            if (saveRecord(line, artifactName, artifactVersion, artifactVariant, artifactUri)) {
                recordsSaved++;
            } else {
                recordsSkipped++;
            }
        }

        return new CorpusIngestResult(
            sourceChunksSaved,
            sourceChunksSkipped,
            recordsSaved,
            recordsSkipped
        );
    }

    public CorpusIngestResult ingestAllTargets(
        Path corpusRootDirectory,
        String artifactName,
        String artifactVersion,
        String artifactVariant,
        String artifactUri
    ) {
        int sourceChunksSaved = 0;
        int sourceChunksSkipped = 0;
        int recordsSaved = 0;
        int recordsSkipped = 0;

        try (Stream<Path> targetDirectories = Files.list(corpusRootDirectory)) {
            for (Path targetDirectory : targetDirectories.toList()) {
                if (!Files.isDirectory(targetDirectory)) {
                    continue;
                }

                CorpusIngestResult result = ingestTarget(
                    targetDirectory,
                    artifactName,
                    artifactVersion,
                    artifactVariant,
                    artifactUri
                );

                sourceChunksSaved += result.sourceChunksSaved();
                sourceChunksSkipped += result.sourceChunksSkipped();
                recordsSaved += result.recordsSaved();
                recordsSkipped += result.recordsSkipped();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read corpus root directory: " + corpusRootDirectory, e);
        }

        return new CorpusIngestResult(
            sourceChunksSaved,
            sourceChunksSkipped,
            recordsSaved,
            recordsSkipped
        );
    }


    private String toJson(Object value, String defaultJson) {
        if (value == null) {
            return defaultJson;
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize corpus json field", e);
        }
    }

    private String defaultLanguage(String language) {
        if (language == null || language.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        return language;
    }

    private GlobalCorpusSource getOrCreateSource(
        String sourceKey,
        String sourceTitle,
        String sourceLicense,
        String artifactName,
        String artifactVersion,
        String artifactVariant,
        String artifactUri
    ) {
        return sourceRepository.findBySourceKey(sourceKey)
            .orElseGet(() -> sourceRepository.save(
                GlobalCorpusSource.create(
                    sourceKey,
                    sourceTitle,
                    sourceLicense,
                    artifactName,
                    artifactVersion,
                    artifactVariant,
                    artifactUri,
                    "{}"
                )
            ));
    }

    private boolean saveSourceChunk(
        CorpusSourceChunkLine line,
        String artifactName,
        String artifactVersion,
        String artifactVariant,
        String artifactUri
    ) {
        if (sourceChunkRepository.existsBySourceChunkId(line.sourceChunkId())) {
            return false;
        }

        GlobalCorpusSource source = getOrCreateSource(
            line.sourceId(),
            line.sourceTitle(),
            line.sourceLicense(),
            artifactName,
            artifactVersion,
            artifactVariant,
            artifactUri
        );

        sourceChunkRepository.save(
            GlobalCorpusSourceChunk.create(
                source,
                line.sourceChunkId(),
                line.target(),
                line.sourceId(),
                line.sourceTitle(),
                line.sourceType(),
                line.sourceUrl(),
                line.sourceVersion(),
                line.sourceLicense(),
                line.chunkIndex(),
                toJson(line.headingPath(), "[]"),
                line.text(),
                line.textHash(),
                line.sourceHash(),
                line.rawTextHash(),
                line.normalizedTextHash(),
                toJson(line.metadata(), "{}"),
                toJson(line.sourceRefs(), "[]")
            )
        );

        return true;
    }

    private boolean saveRecord(
        CorpusRecordLine line,
        String artifactName,
        String artifactVersion,
        String artifactVariant,
        String artifactUri
    ) {
        if (recordRepository.existsByRecordId(line.id())) {
            return false;
        }

        GlobalCorpusSource source = getOrCreateSource(
            artifactName,
            artifactName,
            null,
            artifactName,
            artifactVersion,
            artifactVariant,
            artifactUri
        );

        recordRepository.save(
            GlobalCorpusRecord.create(
                source,
                line.id(),
                line.requestId(),
                line.target(),
                defaultLanguage(line.language()),
                line.recordType(),
                line.difficulty(),
                toJson(line.topicPath(), "[]"),
                toJson(line.tags(), "[]"),
                line.followupStrategy(),
                line.embeddingTextKo(),
                line.questionKo(),
                line.answerKo(),
                line.followupQuestionKo(),
                line.conceptKo(),
                line.interviewUse(),
                toJson(line.rubric(), "{}"),
                toJson(line.followupPatternExt(), "{}"),
                toJson(line.sourceRefs(), "[]"),
                toJson(line.quality(), "{}"),
                toJson(line.createdBy(), "{}")
            )
        );

        return true;
    }

    private <T> List<T> readJsonLines(Path path, Class<T> type) {
        List<T> result = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(path)) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                result.add(objectMapper.readValue(line, type));
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read corpus jsonl file: " + path, e);
        }
    }

}
