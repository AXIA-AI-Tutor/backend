# AGENTS.md

## 프로젝트 맥락

Avatar AI Coach는 한국 AX 센터에서 진행하는 팀 MVP 프로젝트입니다.

이 서비스는 사용자가 면접 또는 발표를 연습할 수 있도록 돕습니다.

1. 이력서, 자기소개서, 포트폴리오, JD, 발표자료 등을 업로드한다.
2. 연습 세션을 생성한다.
3. 업로드한 문서 context를 기반으로 AI avatar 질문을 생성한다.
4. 사용자의 음성 답변을 STT로 transcript로 변환한다.
5. AI feedback card를 생성한다.
6. session report를 저장한다.

현재 기획 문서는 AI가 보조해 만든 초안 성격이 강합니다. 확정된 제품 명세로 100% 신뢰하지 말고, 각 작업이 현재 MVP에 정말 필요한지 계속 확인합니다.

## 팀 구성

- AI: FastAPI, STT, LLM feedback, TTS, model experiment, structured AI response schema
- Frontend: practice flow UI, document upload UI, avatar UI, transcript display, feedback card, report screen
- Backend: Spring Boot API, auth, session management, persistence, AI server integration, DB/ERD decision

## 백엔드 기술 스택

- Java 21
- Spring Boot 4.x
- Gradle Groovy
- PostgreSQL
- Docker Compose
- Spring Data JPA
- Spring Security
- OAuth2 Client
- Lombok
- Validation

## Jira와 브랜치 흐름

팀은 Jira를 사용합니다. 큰 작업은 Epic으로 관리하고, 실제 개발 작업은 Task로 관리합니다.

브랜치 이름, commit message, PR title/body에는 Jira issue key를 포함합니다.

브랜치 이름 규칙:

- `feature/KAN-{issue-number}-{description}`: 기능 개발
- `fix/KAN-{issue-number}-{description}`: 버그 수정
- `chore/KAN-{issue-number}-{description}`: 설정, 문서, 유지보수
- `hotfix/KAN-{issue-number}-{description}`: `main` 기준 긴급 수정

브랜치 흐름:

- `main`: 최종 release branch
- `dev`: 백엔드 개발 통합 branch
- `feature`, `fix`, `chore` branch는 `dev`에서 분기한다.
- PR은 `dev`로 올린다.

Commit message format:

```text
KAN-{issue-number} <type>(<scope>): summary
```

Examples:

```text
KAN-1 feat(auth): Google OAuth login implement
KAN-23 fix(report): report score calculation bug fix
KAN-82 docs(agents): Codex work guide add
```

Commit type:

- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 포맷 변경
- `refactor`: 기능 변경 없는 리팩토링
- `test`: 테스트 추가 또는 수정
- `chore`: 빌드, 설정, 기타 유지보수

## MVP 원칙

항상 MVP 1의 핵심 흐름을 먼저 우선합니다.

```text
document upload -> session creation -> question -> answer/STT -> feedback -> report save
```

MVP 1 흐름이 끝까지 동작하기 전에 MVP 2, MVP 3 기능을 과하게 확장하지 않습니다.

MVP 단계 기준:

- POC: AI pipeline이 실제로 동작하는지 검증한다.
- MVP 1: 사용자가 한 세션을 끝까지 완료할 수 있게 만든다.
- MVP 2: avatar experience, live metrics, RAG, memory를 붙여 차별화한다.
- MVP 3: history, deletion, privacy guidance, fallback handling, demo stability를 보강한다.

## 현재 백엔드 상태

현재 백엔드에는 다음이 구현되어 있습니다.

- Spring Boot project setup
- PostgreSQL Docker Compose setup
- `application.yaml` environment variable configuration
- common `ApiResponse`
- common `ErrorResponse`
- `ErrorCode`, `CustomException`, `GlobalExceptionHandler`
- CORS configuration
- basic `SecurityConfig`
- `BaseTimeEntity` and JPA auditing
- User domain model

현재 user domain:

- `com.ax.avatarcoach.domain.user.entity.User`
- `com.ax.avatarcoach.domain.user.entity.Role`
- `com.ax.avatarcoach.domain.user.entity.OAuthProvider`
- `com.ax.avatarcoach.domain.user.repository.UserRepository`

## 현재 인증 범위

현재 Google social login MVP에서는 다음 범위만 구현합니다.

- Google OAuth2 login만 구현한다.
- 일반 username/password login은 구현하지 않는다.
- JWT, refresh token, Redis는 아직 구현하지 않는다.
- Kakao, Naver login은 아직 구현하지 않는다.
- `users` 단일 테이블 구조를 유지한다.
- 팀에서 명시적으로 설계를 바꾸기 전에는 `social_accounts` table을 만들지 않는다.
- Google 최초 로그인 시 새 user를 저장한다.
- `provider + providerUserId`가 이미 존재하면 기존 user를 재사용한다.

## 패키지 기준

기존 package style을 따릅니다.

```text
com.ax.avatarcoach
|-- domain
|   `-- user
|       |-- controller
|       |-- dto
|       |-- entity
|       |-- repository
|       `-- service
`-- global
    |-- common
    |-- config
    |-- exception
    |-- response
    `-- security
```

Domain-specific code는 `domain` 아래에 둡니다.
공통 infrastructure code는 `global` 아래에 둡니다.

OAuth 관련 security code는 우선 다음 위치를 선호합니다.

```text
com.ax.avatarcoach.global.security.oauth
```

## 코딩 기준

- Entity에는 public setter를 남발하지 않는다.
- Entity 생성은 constructor 또는 static factory method를 우선한다.
- Entity는 생성 시점부터 유효한 상태가 되도록 만든다.
- 성공 응답은 기존 `ApiResponse.success(...)`를 사용한다.
- 예외 처리는 기존 exception 구조를 최대한 활용한다.
- 변경 범위는 현재 Jira task에 맞게 작게 유지한다.
- auth 작업 중에는 관련 없는 domain을 건드리지 않는다.
- Hibernate DDL generation이 꺼져 있으므로, JPA entity에 필요한 table/column은 migration 또는 schema 변경과 함께 관리한다.
- 실제 secret은 commit하지 않는다. 예시는 `.env.example`에 둔다.

## 보안과 개인정보 기준

- Raw video는 기본 저장하지 않는다.
- Uploaded document, voice data, transcript, report는 private user data로 취급한다.
- 감정, 심리, 거짓말, 정신 상태를 진단하는 표현은 피한다.
- 가능한 표현은 observable behavior metric 중심으로 제한한다.
- 예: eye contact ratio, posture stability, speech rate, silence duration, filler word count

## 리뷰와 학습 방식

주요 백엔드 개발자는 주니어 백엔드 취업 준비생입니다.

구현을 안내할 때는 다음을 짧게 설명합니다.

- 어떤 file을 만들거나 수정하는지
- 왜 필요한지
- 어떤 code를 작성하면 되는지
- 작성 후 무엇을 확인하면 되는지

사용자가 step-by-step 진행을 요청하면 한 번에 전체 작업을 구현하지 않습니다. 한 단계씩 안내하고, 사용자의 "다음" 신호를 기다립니다.

Code review에서는 correctness risk, maintainability concern, missing test, MVP scope mismatch를 우선적으로 봅니다.
