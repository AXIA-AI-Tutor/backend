# Answer/Feedback 저장 및 조회 연동 가이드

이 문서는 프론트엔드 서버와 AI API 서버가 Answer/Feedback API를 연동할 때 필요한 저장/조회 흐름과 인증 방식을 정리합니다.

## 1) 전체 데이터 흐름

- 하나의 `Session` 안에 여러 `Answer`가 저장될 수 있습니다.
- `Answer`는 **하나의 질문에 대한 사용자 답변 단위**입니다.
- `Feedback`은 **하나의 Answer에 대한 평가 결과**입니다.
- `Report`는 Answer별 Feedback 목록이 아니라 **Session 전체 종합 결과**입니다.
- 이번 Answer/Feedback API 범위에는 **Report 생성 기능이 포함되지 않습니다**.

요약 관계:

- `Session (1) : (N) Answer`
- `Answer (1) : (N) Feedback`
- `Session (1) : (1) Report` (별도 기능)

## 2) 프론트엔드 조회 흐름

프론트엔드는 사용자 로그인 세션(`JSESSIONID`) 쿠키를 포함해서 아래 API를 순서대로 사용할 수 있습니다.

1. `GET /api/sessions/{sessionId}/answers`
   - 특정 세션의 Answer 목록 조회
2. `GET /api/answers/{answerId}`
   - 특정 Answer 상세 조회
3. `GET /api/answers/{answerId}/feedbacks`
   - 특정 Answer에 연결된 Feedback 목록 조회

### 쿠키 포함 호출 설정

Spring Boot API를 브라우저에서 호출할 때 쿠키가 자동 포함되지 않으면 인증 실패가 발생할 수 있습니다.

- `fetch` 사용 시: `credentials: "include"` 필수
- `axios` 사용 시: `withCredentials: true` 필수

예시:

```ts
// fetch
await fetch(`/api/sessions/${sessionId}/answers`, {
  method: "GET",
  credentials: "include",
});

// axios
await axios.get(`/api/answers/${answerId}/feedbacks`, {
  withCredentials: true,
});
```

## 3) 사용자용 저장 API

사용자(브라우저/프론트엔드)가 저장할 때는 공개 API를 사용합니다.

- `POST /api/answers`
- `POST /api/feedbacks`

인증/인가 규칙:

- `JSESSIONID` 기반 세션 인증이 필요합니다.
- 다른 사용자의 `session` 또는 `answer`에는 접근할 수 없습니다.

## 4) AI API 서버용 Internal API (있는 경우)

AI API 서버는 브라우저 세션이 없으므로, 서버 간 통신 전용 Internal API를 사용해야 할 수 있습니다.

- `POST /internal/sessions/{sessionId}/answers`
- `POST /internal/answers/{answerId}/feedbacks`

인증 규칙:

- `X-Internal-Api-Key` 헤더 필요
- `INTERNAL_API_KEY`는 환경변수로 관리
- 실제 secret 값은 문서/코드 저장소에 기록하지 않음

예시(개념):

```http
POST /internal/sessions/1/answers
X-Internal-Api-Key: ${INTERNAL_API_KEY}
Content-Type: application/json
```

## 5) 요청/응답 예시

### Answer 저장 요청 예시 (`POST /api/answers`)

```json
{
  "sessionId": 1,
  "questionText": "자기소개를 해주세요.",
  "transcript": "저는 백엔드 개발자 김동규입니다...",
  "durationSec": 75,
  "speechRate": 3.2,
  "silenceCount": 2,
  "fillerWordCount": 4,
  "eyeContactScore": 80,
  "postureScore": 85,
  "sttStatus": "COMPLETED",
  "startedAt": "2026-05-06T10:00:00",
  "endedAt": "2026-05-06T10:01:15"
}
```

### Feedback 저장 요청 예시 (`POST /api/feedbacks`)

```json
{
  "answerId": 1,
  "summary": "답변 구조는 명확했지만 구체적인 수치가 부족했습니다.",
  "evidence": "프로젝트 경험을 설명했지만 성과 지표가 부족했습니다.",
  "improvementExample": "예약 API 응답 시간을 30% 개선한 경험처럼 수치 중심으로 보완하면 좋습니다.",
  "structureScore": 80,
  "specificityScore": 65,
  "relevanceScore": 75,
  "deliveryScore": 85
}
```

응답은 프로젝트 공통 응답 포맷(`ApiResponse`)을 사용합니다.

## 6) 주의 사항

- JWT, Refresh Token, Redis는 현재 인증 체계에 포함되지 않습니다.
- 프론트 사용자 인증은 `JSESSIONID` 쿠키 기반입니다.
- AI API 서버는 브라우저 세션이 없으므로 Internal API 사용을 고려해야 합니다.
- 점수(`*_score`) 값은 `0~100` 범위를 사용합니다.
- `Answer`는 `Session`에 속하고, `Feedback`은 `Answer`에 속합니다.
- `turnIndex`, 최대 5턴 제한은 현재 ERD/API 범위에 포함하지 않습니다.
- `Report` 생성/조회는 별도 기능으로 분리되어 있습니다.

## 7) 문서화 제외/보안 범위

- 실제 `INTERNAL_API_KEY` 값은 문서에 기재하지 않습니다.
- OAuth secret, GCP credential 등 실제 비밀값은 문서에 기재하지 않습니다.
- `.env.example`에는 placeholder만 유지합니다.
