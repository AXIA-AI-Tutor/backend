# AXIA AI Tutor — Backend

AI 면접·발표 코칭 서비스의 백엔드. Spring Boot가 인증·세션·데이터 정합성과
FastAPI AI 서버 연동(AI Gateway)을 담당합니다.

🔗 프로젝트 전체 소개: https://github.com/AXIA-AI-Tutor

## 서비스
문서 업로드 → 아바타 질문 → 음성 답변 → STT 전사·AI 피드백 → 세션 리포트를
제공하는 AI 면접 코칭 서비스.

## 기술 스택
- Java · Spring Boot · Spring Security · Spring Data JPA · Flyway
- PostgreSQL · pgvector (vector(1024), HNSW cosine)
- RestClient → FastAPI(STT·LLM·Embedding) 연동 · GCS · Docker

## 아키텍처
FE는 AI 서버를 직접 호출하지 않고 Spring API만 호출.
Spring이 인증·세션 상태·저장·AI Gateway를 담당하고, FastAPI가 STT/LLM/Embedding 실행.

## 핵심 구현
- **AI Gateway** — `AiGatewayClient`(RestClient)가 FastAPI 계약(/questions·/stt·/turn·/reports·/embeddings)을 집중하고, AI 서버 오류를 502/503으로 변환.
- **Turn Context** — Answer/Feedback을 source of truth로 `previousTurns`를 구성해 다음 질문 생성 요청에 전달.
- **RAG** — pgvector 코사인 유사도 검색(HNSW) 결과를 `rag_context`로 변환해 AI 요청에 전달.
