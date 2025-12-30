# Auth / Account 담당 범위 정리 (최종 · 코드 반영본)

## 1. 역할 정의

Auth/Account 도메인의 책임은 다음으로 한정합니다.

* Firebase 기반 **사용자 인증**
* 서버 내부에서 사용할 **계정(Account) 식별자 제공**
* 다른 도메인이 신뢰할 수 있는 **인증·인가 규약 제공**

멤버/프로필 데이터 관리 책임은 포함하지 않습니다.

---

## 2. 구현 완료 항목

### 2.1 인증 인프라

* Firebase Auth + Firebase Admin SDK 기반 인증 구조 확정
* 서버에서 Firebase ID Token 검증
* 검증 결과로 다음 정보 추출

    * `firebaseUid`
    * `email` (존재 시)

---

### 2.2 인증 필터 (`/me/**`)

* 보호 경로: `/me/**`

* 요청 헤더:

    * `Authorization: Bearer <Firebase ID Token>`

* 성공 시:

    * request attribute에 다음 값 저장

        * `firebaseUid`
        * `firebaseEmail`

* 실패 시:

    * HTTP 401
    * 공통 에러 응답 포맷 반환

```json
{
  "code": "UNAUTHORIZED",
  "message": "Invalid or missing token."
}
```

---

### 2.3 공통 에러 처리

* 에러 응답 포맷 통일

```json
{
  "code": "STRING_CODE",
  "message": "Human readable message"
}
```

* 처리 규칙:

    * 인증 실패 → 401 (`UNAUTHORIZED`)
    * 요청 검증 오류 → 400 (`VALIDATION_ERROR`)
    * 서버 내부 상태 오류 → 500 (`INTERNAL_ERROR`)

* 처리 위치:

    * 필터 단계 401: 인증 필터에서 직접 처리
    * 컨트롤러/서비스 단계: `GlobalExceptionHandler`에서 처리

---

### 2.4 테스트 엔드포인트

* `GET /me/ping`

    * 목적: 인증 필터 및 토큰 검증 확인
    * 토큰 없음 → 401
    * 토큰 유효 → 200 + `firebaseUid`, `firebaseEmail` 확인

---

## 3. Auth/Account 도메인에서 하지 않는 것

* 멤버 프로필 CRUD (`members`)
* 이름, 기수, 파트, 스킬, 링크 등 서비스 프로필 데이터
* 공개 멤버 리스트/상세 조회 API
* `/me/member` 등 프로필 수정·조회 API

---

## 4. Account(UserAuth) 구현 상태

Account는 **인증과 내부 식별을 위한 최소 정보만 관리**합니다.

### 4.1 관리 필드

* `userId` (내부 식별자, UUID)
* `firebaseUid` (Firebase 인증 식별자)
* `email`
* `role`
* `passwordHash` (Firebase 사용으로 NULL/미사용)
* `createdAt`
* `updatedAt`

---

### 4.2 저장소 및 저장 방식

* 저장소: **Cloud Firestore**
* 컬렉션: `user_auth`
* 문서 ID: `firebaseUid`

저장 전략: **get-or-create(upsert)**

* 첫 인증된 `/me/**` 요청 시:

    * 문서가 없으면 생성
    * 있으면 기존 문서 반환

* 기존 문서에 email이 없고,
  토큰에 email이 존재하면 email 보정 업데이트 수행

> 문서 상의 `firebase_uid`, `password_hash` 표기는 **개념적 표현**이며,
> 실제 구현에서는 camelCase 필드명을 사용한다.

---

### 4.3 Account 조회 API

#### `GET /me/account`

설명:

* 인증된 사용자의 Account(UserAuth) 정보를 조회한다.
* 첫 요청 시 Account가 없으면 get-or-create 방식으로 생성된다.

인증:

* 필요 (`Authorization: Bearer <Firebase ID Token>`)

Response 200:

```json
{
  "userId": "uuid",
  "firebaseUid": "string",
  "email": "string | null",
  "role": "USER"
}
```

비고:

* `passwordHash`는 응답에 포함하지 않는다.

---

## 5. Auth/Account 고정 규약 (머지 기준)

다른 도메인이 의존하는 **고정 계약**입니다.

1. 인증 헤더

    * `Authorization: Bearer <Firebase ID Token>`

2. 보호 경로

    * `/me/**`

3. 사용자 식별자 전달 방식

    * request attribute:

        * `"firebaseUid"`
        * `"firebaseEmail"`

4. 계정 저장 규약

    * Firestore `user_auth/{firebaseUid}`

5. 에러 응답 포맷

    * `{ code, message }`

---

## 6. 현재 상태 요약

> **Auth/Account 도메인은
> Firebase 인증 인프라, `/me/**` 보호, 공통 에러 처리,
> Account(UserAuth) get-or-create 저장 및 조회까지 구현 완료 상태입니다.**
