# Cloud Run Corpus Job Guide

This guide describes how to run the global corpus ingest and embedding jobs for the RAG corpus.

## Purpose

The backend application normally runs as a Cloud Run Service.
Corpus loading should run as a separate Cloud Run Job so that large data ingest and embedding work does not slow down or break normal API startup.

The job flow is:

1. Upload a corpus zip artifact to Cloud Storage.
2. Run the corpus ingest job.
3. Run the corpus embedding job.
4. Verify that records and embeddings exist in PostgreSQL pgvector.

## Uploaded Artifact

Current demo artifact:

```text
gs://avatar-coach-documents-dev/rag-corpus/axia-corpus-20260512.zip
```

The zip contains multiple corpus roots, for example:

```text
backend_ai-ml/data/corpus
Frontend_Data/data/corpus
Fullstack_QA/data/corpus
Devops_Mobile/data/corpus
```

Each corpus root contains target directories with `records.jsonl`.

## Ingest Job

The ingest job downloads the corpus zip from GCS, extracts it, finds all corpus roots, and saves corpus records to PostgreSQL.

Required environment variables:

```text
SPRING_PROFILES_ACTIVE=prod
CORPUS_INGEST_JOB_ENABLED=true
CORPUS_EMBEDDING_JOB_ENABLED=false
GCS_STORAGE_ENABLED=true
CORPUS_ARTIFACT_URI=gs://avatar-coach-documents-dev/rag-corpus/axia-corpus-20260512.zip
CORPUS_ARTIFACT_NAME=axia-corpus-20260512
CORPUS_ARTIFACT_VERSION=20260512
CORPUS_ARTIFACT_VARIANT=demo
```

The job also needs the same database and AI gateway environment variables as the backend service when the application context starts.

Expected log line:

```text
Completed corpus ingest job. sourceChunksSaved=..., sourceChunksSkipped=..., recordsSaved=..., recordsSkipped=...
```

## Embedding Job

The embedding job finds `global_corpus_records` rows where `embedding IS NULL`, calls the configured embedding provider, and stores vectors in pgvector.

Required environment variables:

```text
SPRING_PROFILES_ACTIVE=prod
CORPUS_INGEST_JOB_ENABLED=false
CORPUS_EMBEDDING_JOB_ENABLED=true
RAG_EMBEDDING_PROVIDER=ai_worker
CORPUS_EMBEDDING_JOB_BATCH_SIZE=100
CORPUS_EMBEDDING_JOB_MAX_BATCHES=300
```

For the current 24k processed corpus records, `batch-size=100` and `max-batches=300` can process up to 30k rows in a single run.

Expected log line:

```text
Completed corpus embedding job. totalEmbeddedCount=...
```

If the embedding server is temporarily unavailable, rerun the same job. Already embedded rows are skipped because the job only selects rows where `embedding IS NULL`.

## Verification Queries

Run these queries against the production PostgreSQL database after the jobs complete.

```sql
SELECT COUNT(*) AS total_records
FROM global_corpus_records;
```

```sql
SELECT
    COUNT(*) AS total_records,
    COUNT(embedding) AS embedded_records,
    COUNT(*) - COUNT(embedding) AS pending_records
FROM global_corpus_records;
```

```sql
SELECT vector_dims(embedding) AS dims
FROM global_corpus_records
WHERE embedding IS NOT NULL
LIMIT 1;
```

Expected `dims` value:

```text
1024
```

## Safety Notes

- Do not set `CORPUS_INGEST_JOB_ENABLED=true` or `CORPUS_EMBEDDING_JOB_ENABLED=true` on the normal Cloud Run Service.
- Use Cloud Run Job for corpus work, not Flyway migration.
- The ingest job can be rerun safely for existing `record_id` values because duplicate records are skipped.
- The embedding job can be rerun safely because it only processes rows with `embedding IS NULL`.
