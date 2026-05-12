package com.ax.avatarcoach.domain.corpus.service;

import com.ax.avatarcoach.domain.corpus.dto.CorpusIngestResult;
import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusRecord;
import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusSource;
import com.ax.avatarcoach.domain.corpus.entity.GlobalCorpusSourceChunk;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusRecordRepository;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusSourceChunkRepository;
import com.ax.avatarcoach.domain.corpus.repository.GlobalCorpusSourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GlobalCorpusIngestServiceTest {

    private static final String ESCAPED_NULL_BYTE = "\\u" + "0000";
    private static final String NULL_BYTE = String.valueOf((char) 0);

    @TempDir
    private Path tempDir;

    @Test
    void ingestTargetRemovesNullBytesBeforeSavingCorpusRows() throws IOException {
        GlobalCorpusSourceRepository sourceRepository = mock(GlobalCorpusSourceRepository.class);
        GlobalCorpusRecordRepository recordRepository = mock(GlobalCorpusRecordRepository.class);
        GlobalCorpusSourceChunkRepository sourceChunkRepository = mock(GlobalCorpusSourceChunkRepository.class);

        when(sourceRepository.findBySourceKey(anyString())).thenReturn(Optional.empty());
        when(sourceRepository.save(any(GlobalCorpusSource.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        GlobalCorpusIngestService service = new GlobalCorpusIngestService(
            sourceRepository,
            recordRepository,
            sourceChunkRepository,
            new ObjectMapper()
        );

        Path targetDirectory = tempDir.resolve("backend");
        Files.createDirectories(targetDirectory);
        Files.writeString(
            targetDirectory.resolve("source_chunks.jsonl"),
            sourceChunkLineWithNullBytes()
        );
        Files.writeString(
            targetDirectory.resolve("records.jsonl"),
            recordLineWithNullBytes()
        );

        CorpusIngestResult result = service.ingestTarget(
            targetDirectory,
            "artifact" + NULL_BYTE,
            "20260512" + NULL_BYTE,
            "demo" + NULL_BYTE,
            "gs://bucket/corpus.zip" + NULL_BYTE
        );

        assertThat(result.sourceChunksSaved()).isEqualTo(1);
        assertThat(result.recordsSaved()).isEqualTo(1);

        ArgumentCaptor<GlobalCorpusSourceChunk> sourceChunkCaptor =
            ArgumentCaptor.forClass(GlobalCorpusSourceChunk.class);
        verify(sourceChunkRepository).save(sourceChunkCaptor.capture());
        GlobalCorpusSourceChunk sourceChunk = sourceChunkCaptor.getValue();

        assertClean(sourceChunk.getSourceChunkId());
        assertClean(sourceChunk.getSourceKey());
        assertClean(sourceChunk.getSourceTitle());
        assertClean(sourceChunk.getText());
        assertClean(sourceChunk.getHeadingPathJson());
        assertClean(sourceChunk.getMetadataJson());

        ArgumentCaptor<GlobalCorpusRecord> recordCaptor =
            ArgumentCaptor.forClass(GlobalCorpusRecord.class);
        verify(recordRepository).save(recordCaptor.capture());
        GlobalCorpusRecord record = recordCaptor.getValue();

        assertClean(record.getRecordId());
        assertClean(record.getTarget());
        assertClean(record.getLanguage());
        assertClean(record.getEmbeddingTextKo());
        assertClean(record.getQuestionKo());
        assertClean(record.getTopicPathJson());
        assertClean(record.getRubricJson());
    }

    private String sourceChunkLineWithNullBytes() {
        return """
            {"source_chunk_id":"chunk-1%s","target":"BACKEND%s","source_id":"source-1%s","source_title":"FastAPI%s","source_type":"docs%s","source_url":"https://example.com%s","source_version":"main%s","source_license":"MIT%s","chunk_index":1,"heading_path":["BackgroundTasks%s"],"text":"chunk text%s","text_hash":"hash%s","source_hash":"source-hash%s","raw_text_hash":"raw-hash%s","normalized_text_hash":"normalized-hash%s","metadata":{"note":"metadata%s"},"source_refs":[{"source_id":"source-1%s"}]}
            """.formatted(
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE
        );
    }

    private String recordLineWithNullBytes() {
        return """
            {"id":"record-1%s","request_id":"request-1%s","target":"BACKEND%s","language":"ko%s","record_type":"followup_pattern%s","difficulty":"NORMAL%s","topic_path":["backend%s","fastapi%s"],"tags":["background%s"],"followup_strategy":"role%s","embedding_text_ko":"embedding text%s","question_ko":"question%s","answer_ko":"answer%s","followup_question_ko":"followup%s","concept_ko":"concept%s","interview_use":"interview%s","rubric":{"must_include":["role%s"]},"followup_pattern_ext":{"anchor_examples_ko":["FastAPI%s"]},"source_refs":[{"source_id":"source-1%s"}],"quality":{"score":"ok%s"},"created_by":{"name":"pipeline%s"}}
            """.formatted(
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE,
            ESCAPED_NULL_BYTE
        );
    }

    private void assertClean(String value) {
        assertThat(value)
            .doesNotContain(NULL_BYTE)
            .doesNotContain(ESCAPED_NULL_BYTE);
    }
}
