# GCS Signed URL 문서 업로드 연동 가이드

이 문서는 프론트엔드가 **GCS Signed URL 방식**으로 문서를 업로드할 때 필요한 백엔드 연동 흐름과 주의사항을 정리합니다.

> 보안 주의: 실제 GCP bucket 이름, OAuth secret, service account key 등 민감 정보는 문서/코드에 하드코딩하지 않습니다.

## 1) 전체 업로드 흐름

현재 백엔드 기준 업로드 흐름은 아래 순서입니다.

1. **프론트 → Spring Boot**: `POST /api/documents/upload-url`
   - 요청 본문에 `sessionId`, `docType`, `originalFileName`, `fileType`, `fileSize`를 포함합니다.
2. **Spring Boot**
   - 로그인 사용자 확인(OAuth2 session)
   - 요청 `sessionId`가 로그인 사용자 소유 세션인지 검증
   - `Document`를 `uploadStatus=PENDING`으로 저장
   - 업로드용 `signedUrl` 반환
3. **프론트 → GCS**: 반환된 `signedUrl`로 `PUT` 업로드
4. **프론트 → Spring Boot**: `POST /api/documents/{documentId}/complete`
5. **Spring Boot**
   - GCS object metadata 조회
   - DB의 파일 정보와 비교 검증 (size, content-type)
   - 검증 성공 시 `uploadStatus=UPLOADED`, `status=READY_FOR_AI`로 전환

---

## 2) 세션 쿠키 기반 인증 주의

현재 로그인 방식은 **Spring Security OAuth2 + session cookie** 기반입니다.

- JWT / Refresh Token / Redis 기반 인증을 사용하지 않습니다.
- 따라서 프론트에서 Spring Boot API 호출 시 쿠키를 반드시 포함해야 합니다.

### fetch 사용 시

```ts
await fetch(`${API_BASE_URL}/api/documents/upload-url`, {
  method: "POST",
  credentials: "include", // 필수
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify(payload),
});
```

### axios 사용 시

```ts
await axios.post(`${API_BASE_URL}/api/documents/upload-url`, payload, {
  withCredentials: true, // 필수
});
```

쿠키가 빠지면 로그인 직후에도 서버에서는 미인증 사용자로 처리될 수 있습니다.

---

## 3) 프론트 연동 예시

아래는 권장 순서 예시입니다.

### 3-1. upload-url 발급 요청

```ts
type UploadUrlRequest = {
  sessionId: number;
  docType: "RESUME" | "PORTFOLIO" | "COVER_LETTER" | "JD" | "PRESENTATION" | "ETC";
  originalFileName: string;
  fileType: string; // 예: application/pdf
  fileSize: number;
};

const uploadUrlRes = await fetch(`${API_BASE_URL}/api/documents/upload-url`, {
  method: "POST",
  credentials: "include",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    sessionId,
    docType: "RESUME",
    originalFileName: file.name,
    fileType: file.type,
    fileSize: file.size,
  } satisfies UploadUrlRequest),
});

const uploadUrlJson = await uploadUrlRes.json();
const { documentId, uploadUrl, requiredHeaders } = uploadUrlJson.data;
```

### 3-2. GCS PUT 업로드

```ts
await fetch(uploadUrl, {
  method: "PUT",
  headers: {
    "Content-Type": file.type,
    // 백엔드가 내려준 requiredHeaders를 우선 사용
    ...requiredHeaders,
  },
  body: file,
});
```

### 3-3. complete 요청

```ts
await fetch(`${API_BASE_URL}/api/documents/${documentId}/complete`, {
  method: "POST",
  credentials: "include",
});
```

---

## 4) GCS PUT 주의사항

- Signed URL 생성 시점의 `Content-Type`과 실제 PUT 업로드의 `Content-Type`은 반드시 동일해야 합니다.
- Signed URL에는 만료 시간이 있습니다.
- 만료된 URL로 업로드가 실패하면 `POST /api/documents/upload-url`을 다시 호출해 URL을 재발급받아야 합니다.

---

## 5) 분리 배포(도메인 분리) 고려사항

프론트엔드, Spring Boot 백엔드, AI API 서버는 각각 별도 배포를 전제로 설계하고 있습니다.

- 프론트 도메인과 백엔드 도메인이 다르면:
  - Spring Security CORS에서 `allowCredentials(true)` 및 허용 Origin 설정이 필요합니다.
  - 브라우저 쿠키 정책(`SameSite`, `Secure`)도 배포 환경에 맞게 확인해야 합니다.
- 프론트에서 GCS로 직접 PUT 요청하므로:
  - 버킷 CORS 설정이 필요할 수 있습니다(허용 Origin / Method PUT / Headers).

---

## 6) AI API 연동 범위

현재 문서 업로드 기능은 **AI API 호출을 포함하지 않습니다.**

- 업로드 완료 시점에는 문서 상태를 `READY_FOR_AI`로 전환합니다.
- 이후 AI API가 `storageBucket + storagePath`를 기반으로 문서를 처리하도록 후속 파이프라인을 연동할 예정입니다.
