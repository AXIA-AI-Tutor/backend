# Google OAuth 로그인 테스트 및 검증

## 목적

Google OAuth 기반 세션 로그인이 정상 동작하는지 확인한다.

검증 범위:

- Google OAuth 로그인 시작
- 로그인 성공 후 redirect
- `users` 테이블 저장
- 동일 Google 계정 재로그인 시 중복 저장 방지
- 현재 로그인 사용자 조회 API 확인
- 비로그인 상태 접근 차단 확인

## 사전 준비

### 1. PostgreSQL 실행

```bash
docker compose up -d
```

### 2. 로컬 환경변수 설정

`.env.example`을 참고해 로컬 `.env`에 실제 값을 설정한다.

```env
DB_URL=jdbc:postgresql://localhost:5432/avatar_coach
DB_USERNAME=avatar
DB_PASSWORD=your-local-password

GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

주의:

- 실제 `.env`는 Git에 커밋하지 않는다.
- Google OAuth 값은 Google Cloud Console에서 발급받은 실제 값을 사용한다.

### 3. Google Cloud Console 설정

OAuth Client의 Authorized redirect URI에 아래 값을 등록한다.

```text
http://localhost:8080/login/oauth2/code/google
```

OAuth 앱이 Testing 상태라면, 로그인 테스트에 사용할 Google 계정을 Test users에 추가한다.

## 테스트 절차

### 1. 서버 실행

IntelliJ 또는 Gradle로 Spring Boot 서버를 실행한다.

서버 주소:

```text
http://localhost:8080
```

### 2. Google 로그인 시작

브라우저에서 아래 URL에 접속한다.

```text
http://localhost:8080/oauth2/authorization/google
```

기대 결과:

- Google 로그인 화면으로 이동한다.
- 로그인 성공 후 백엔드 callback URL로 돌아온다.
- 최종적으로 `/api/health` 응답을 확인한다.

예상 응답:

```json
{
  "success": true,
  "data": "OK",
  "message": "요청이 성공했습니다."
}
```

### 3. users 테이블 저장 확인

PostgreSQL에서 `users` 테이블을 조회한다.

```sql
SELECT id, email, nickname, provider, provider_user_id, role, created_at
FROM users;
```

기대 결과:

- 로그인한 Google 계정에 해당하는 row가 생성된다.
- `provider` 값은 `GOOGLE`이다.
- `provider_user_id`에는 Google `sub` 값이 저장된다.
- `role` 값은 `USER`이다.

### 4. 중복 저장 방지 확인

같은 Google 계정으로 다시 로그인한다.

```text
http://localhost:8080/oauth2/authorization/google
```

다시 `users` 테이블을 조회한다.

```sql
SELECT provider, provider_user_id, COUNT(*)
FROM users
GROUP BY provider, provider_user_id;
```

기대 결과:

- 같은 `provider + provider_user_id` 조합의 count가 `1`이다.
- 동일 계정 재로그인 시 users row가 중복 생성되지 않는다.

### 5. 현재 로그인 사용자 조회 API 확인

로그인된 브라우저에서 아래 URL에 접속한다.

```text
http://localhost:8080/api/users/me
```

기대 응답:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "user",
    "profileImageUrl": "https://...",
    "role": "USER"
  },
  "message": "요청이 성공했습니다."
}
```

### 6. 비로그인 상태 접근 확인

시크릿 창 또는 쿠키가 없는 브라우저에서 아래 URL에 접속한다.

```text
http://localhost:8080/api/users/me
```

기대 결과:

- 로그인되지 않은 사용자는 현재 사용자 정보를 조회할 수 없다.
- Spring Security 로그인 흐름으로 redirect되거나 인증 실패 응답이 발생한다.

## 확인된 MVP 범위

- JWT, Refresh Token, Redis는 사용하지 않는다.
- 현재 로그인 방식은 Spring Security OAuth2 session 기반이다.
- 브라우저는 `JSESSIONID` cookie로 로그인 상태를 유지한다.
- 프론트엔드는 API 요청 시 cookie가 포함되도록 설정해야 한다.

프론트 요청 예시:

```javascript
fetch("http://localhost:8080/api/users/me", {
  credentials: "include",
});
```

## 주의 사항

- 현재 `ddl-auto: update`는 로컬 MVP 테스트 편의를 위한 설정이다.
- 추후 운영 또는 협업 안정화 단계에서는 Flyway/Liquibase 같은 migration 도입을 검토한다.
- Vercel frontend와 GCP backend를 분리 배포할 경우 CORS와 cookie 설정을 추가로 점검해야 한다.
