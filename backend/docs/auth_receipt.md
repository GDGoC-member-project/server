# Auth / Account 도메인 정리 (코드 기준 최소 문서)

## 1. 역할 범위

Auth/Account 도메인은 다음 책임만 가진다.

- Firebase ID Token 검증을 통한 **사용자 인증**
- 내부에서 사용하는 Account(UserAuth) **식별자(userId) 발급 및 조회**
- 다른 도메인이 신뢰할 수 있는 **인증/계정 규약 제공**

---

## 2. 인증 필터 및 보호 경로

### 2.1 보호 경로

- 보호 대상: `/api/v1/me/**`

### 2.2 Firebase 인증

- 클라이언트는 Firebase에서 발급한 ID Token 을 헤더로 전송한다.
- 요청 헤더:

  ```text
  Authorization: Bearer <Firebase ID Token>
  ```

- 필터는 토큰을 검증한 뒤, 성공 시 다음 request attribute 를 설정한다.

  - `firebaseUid`
  - `firebaseEmail` (nullable)

### 2.3 로컬/디버그 모드

- 설정값 `auth.firebase.enabled=false` 인 경우, Firebase 검증을 우회하고 디버그 헤더를 사용한다.
- 디버그 헤더:

  - `X-Debug-Firebase-Uid`
  - `X-Debug-Firebase-Email`

- 값이 없으면 `firebaseUid = "debug-uid"`, `firebaseEmail = null` 로 간주하고 필터를 통과시킨다.

### 2.4 인증 실패 응답

- 토큰 없음/형식 오류/검증 실패 시 필터에서 바로 401 응답을 내린다.
- 응답 포맷은 `BaseResponse<Void>` ERROR 형태로 고정된다.

```json
{
  "status": "ERROR",
  "data": null,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid or missing token.",
    "details": null
  }
}
```

---

## 3. Account(UserAuth) 저장 구조

### 3.1 저장소 및 테이블

- 저장소: **MySQL (InnoDB)**
- 테이블: `gdgoc_archive.user_auth`

주요 컬럼 :

- `user_id` CHAR(36) PK — 서비스 내부 식별자 (UUID)
- `firebase_uid` VARCHAR(128) UNIQUE NOT NULL
- `email` VARCHAR(255) NULL
- `password_hash` VARCHAR(255) NULL (Firebase 사용으로 실질적으로 미사용)
- `role` VARCHAR(50) NOT NULL — Enum(Role) 값 (`LEAD` | `CORE` | `MEMBER`)을 문자열로 저장

엔티티 클래스: `com.gdgoc.archive.account.UserAuth`

```java
@Column(name = "role", length = 50, nullable = false)
@Enumerated(EnumType.STRING)
private Role role; // LEAD, CORE, MEMBER
```

### 3.2 저장 전략 (get-or-create)

- 첫 `/api/v1/me/account` 호출 시:
  - 해당 `firebase_uid` 로 row 가 없으면:
    - 새로운 `user_id` (UUID) 발급
    - `role = MEMBER` 기본값으로 row 생성
  - row 가 이미 있으면:
    - 기존 row 를 그대로 사용
- 기존 row 에 `email` 이 없고, 토큰에 email 이 있다면:
  - email 값을 보정 UPDATE 한다.

구현 클래스: `com.gdgoc.archive.account.AccountService#getOrCreate(String firebaseUid, String email)`

---

## 4. 공개 API 스펙

### 4.1 공통 응답 스키마 (BaseResponse<T>)

모든 API 응답은 `BaseResponse<T>` 로 래핑된다.

```json
{
  "status": "SUCCESS" | "ERROR",
  "data": {},
  "error": {
    "code": "STRING",
    "message": "STRING",
    "details": {}
  }
}
```

- 성공 시: `status = "SUCCESS"`, `data = <payload>`, `error = null`
- 실패 시: `status = "ERROR"`, `data = null`, `error` 에 상세 정보

### 4.2 `GET /api/v1/me/ping`

- 컨트롤러: `com.gdgoc.archive.auth.AuthTestController`
- 목적: 인증 필터 및 Firebase 토큰 검증/디버그 헤더 동작 확인
- 인증:
  - prod: `Authorization: Bearer <ID_TOKEN>` 필요
  - local(dev): `auth.firebase.enabled=false` 이면 디버그 헤더로 대체 가능

성공 응답 (BaseResponse<PingResponse>):

```json
{
  "status": "SUCCESS",
  "data": {
    "ok": true,
    "firebaseUid": "firebase-uid",
    "email": "user@example.com"
  },
  "error": null
}
```

### 4.3 `GET /api/v1/me/account`

- 컨트롤러: `com.gdgoc.archive.account.AccountController`
- 서비스: `AccountService#getOrCreate(firebaseUid, email)`
- 설명:
  - 인증된 사용자의 Account(UserAuth) 정보를 조회한다.
  - 첫 요청 시 해당 `firebase_uid` row 가 없으면 get-or-create 방식으로 생성한다.
- 인증:
  - prod: `Authorization: Bearer <ID_TOKEN>` 필요
  - local(dev): `auth.firebase.enabled=false` 일 때 디버그 헤더 사용 가능

성공 응답 (BaseResponse<AccountResponse>):

```json
{
  "status": "SUCCESS",
  "data": {
    "userId": "uuid",
    "firebaseUid": "firebase-uid",
    "email": "string | null",
    "role": "MEMBER"
  },
  "error": null
}
```

비고:

- `role` 값은 Enum(Role) 기준 `LEAD` | `CORE` | `MEMBER` 중 하나이며,
  신규 생성 시 기본값은 `"MEMBER"` 이다.
- `passwordHash` 는 응답에 포함되지 않는다.

---

## 5. 고정 규약 (다른 도메인에서 의존 가능한 부분)

1. 인증 헤더

   - `Authorization: Bearer <Firebase ID Token>`

2. 보호 경로

   - `/api/v1/me/**`

3. 사용자 식별자 전달 방식

   - request attribute:
     - `"firebaseUid"`
     - `"firebaseEmail"`

4. 계정 저장 규약

   - MySQL `user_auth` 테이블 row (`user_id`, `firebase_uid` 기반)

5. 에러 응답 포맷

   - BaseResponse ERROR:

   ```json
   {
     "status": "ERROR",
     "data": null,
     "error": {
       "code": "STRING",
       "message": "Human readable message",
       "details": {}
     }
   }
   ```

