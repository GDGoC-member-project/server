## Auth / Account 담당 범위 정리 (현재 기준)

### 1. 역할 정의

Auth/Account 도메인의 책임은 **“사용자 인증과 계정 식별을 안정적으로 제공하는 것”**이며,
실제 서비스 프로필 데이터(멤버 정보)는 다루지 않습니다.

---

### 2. 구현 완료 항목

#### 2.1 인증 인프라

* Firebase Auth 기반 인증 구조 확정
* Firebase Admin SDK 초기화 완료
* 클라이언트는 Firebase Google 로그인 후 **Firebase ID Token**을 발급받아 서버로 전달
* 서버는 ID Token을 검증하여 `firebaseUid`를 획득

#### 2.2 인증 필터(`/me/**`)

* 보호 경로: `/me/**`
* 인증 방식:

    * Header: `Authorization: Bearer <Firebase ID Token>`
    * 토큰 검증 성공 시:

        * 요청 컨텍스트에 `firebaseUid` 저장 (`request.setAttribute("firebaseUid", uid)`)
    * 실패 시:

        * HTTP 401
        * 공통 에러 응답 포맷 반환

#### 2.3 공통 에러 처리

* 에러 응답 포맷 통일:

  ```json
  {
    "code": "STRING_CODE",
    "message": "Human readable message"
  }
  ```
* 필터 단계(401):

    * 인증 필터에서 직접 JSON 응답 반환
* 컨트롤러 단계(400/401):

    * `GlobalExceptionHandler`에서 공통 처리

#### 2.4 테스트용 엔드포인트

* `GET /me/ping`

    * 목적: 인증 필터 동작 여부 확인
    * 토큰 없음 → 401
    * 토큰 있음 → 200 + `firebaseUid` 확인 가능

---

### 3. Auth 도메인에서 **하지 않는 것**

아래 항목들은 **Auth/Account 범위를 벗어남**으로 명확히 제외합니다.

* 멤버 프로필 CRUD (`members` 테이블)
* 이름/기수/파트/스킬/링크 등 “서비스 프로필 데이터”
* 공개 멤버 리스트/상세 조회 API
* 마이페이지 프로필 UI용 데이터 제공 (`/me/member`)

→ 위 항목들은 **Members/Profile 도메인 책임**

---

### 4. “마이페이지(Account)”에 대한 해석 (노션 기준)

노션에 정의된 **마이페이지**는 다음 의미로 해석됩니다.

* 계정(Account) 기준 정보:

    * `userId` (내부 식별자)
    * `firebase_uid` (실제 인증 식별자)
    * `email`
    * `role`
    * `password_hash` (Firebase 사용으로 **NULL 또는 미사용**)

현재 단계에서는:

* **계정 테이블(UserAuth) 생성 여부는 ‘회의 합의에 따라 가능성 열어둠’**
* 실제 구현은 아직 착수하지 않음
* 저장 트리거는 “첫 인증된 `/me/**` 요청 시 upsert”가 자연스러운 방향

---

### 5. Auth/Account 도메인 규약 (고정 사항)

이 규약을 기준으로 다른 도메인이 Auth를 신뢰하고 사용합니다.

1. 인증 헤더

    * `Authorization: Bearer <Firebase ID Token>`

2. 보호 경로

    * `/me/**`

3. 사용자 식별자 전달

    * request attribute key: `"firebaseUid"`

4. 에러 응답 포맷

    * `{ code, message }`

---

### 6. 현재 상태 한 줄 요약

> **Auth/Account 도메인은
> Firebase 기반 인증 + `/me/**` 보호 + 공통 에러 처리까지 구현 완료 상태이며,
> 계정(UserAuth) 저장은 회의 합의에 따라 다음 단계에서 최소 구현 예정인 상태입니다.**
