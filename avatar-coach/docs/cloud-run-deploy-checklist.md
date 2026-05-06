# Cloud Run 배포 체크리스트 (Spring Boot Backend)

## 1) MVP 배포 범위
- 이번 배포 대상은 Spring Boot 백엔드 단일 서비스입니다.
- 프론트엔드, AI API 서버는 별도 서비스로 배포합니다.

## 2) OAuth Redirect URI 설정
Cloud Run 배포 후 백엔드 도메인이 바뀌면 Google OAuth Redirect URI를 반드시 갱신해야 합니다.

- Redirect URI 형식:
  - `https://{backend-domain}/login/oauth2/code/google`
- 예시:
  - `https://api.example.com/login/oauth2/code/google`

> 주의: `GOOGLE_CLIENT_SECRET` 같은 실제 secret 값은 저장소에 커밋하지 않습니다.

## 3) 세션 기반 인증(JSESSIONID) 배포 주의사항
현재 인증 방식은 Spring Security OAuth2 + 세션(JSESSIONID) 기반입니다.
Cloud Run 다중 인스턴스에서 요청이 다른 인스턴스로 분산되면 세션이 유지되지 않을 수 있습니다.

MVP 운영 시 아래 중 하나를 적용합니다.
- `max instances=1`로 운영
- Cloud Run session affinity 사용

향후 확장 TODO(이번 작업 범위 아님)
- Redis + Spring Session 도입
- Stateless JWT 인증 전환

## 4) GCS 정책
- 기본값은 `GCS_STORAGE_ENABLED=false` 입니다.
- GCS 비활성화 상태에서도 문서 metadata 저장/조회 API는 정상 동작합니다.
- 업로드 URL 발급/업로드 완료 검증처럼 실제 저장소 접근이 필요한 API는 비활성화 상태에서 실패하도록 설계되어 있습니다.

## 5) 필수 환경변수
Cloud Run 배포 시 아래 환경변수를 설정합니다.

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `GCS_STORAGE_ENABLED`
- `GCS_BUCKET_NAME`
- `GCS_DOCUMENT_PREFIX`
- `GCS_SIGNED_URL_EXPIRATION_MINUTES`
- `INTERNAL_API_KEY`

선택(환경에 따라)
- `OAUTH2_SUCCESS_REDIRECT_URL`
- `AI_SERVER_BASE_URL`
- `PORT` (Cloud Run이 자동 주입)
- `GOOGLE_APPLICATION_CREDENTIALS` (런타임 방식에 따라 필요 시)
