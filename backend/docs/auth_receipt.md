# 1. 목적/범위 (업데이트)

## 1.1 목적

* 공개 멤버 리스트/상세 조회 제공
* Firebase Google 로그인 기반으로 “내 프로필”을 생성/수정(업서트) 제공
* 내 프로필 조회 제공(수정 UX 지원)
* 기수/파트 중심의 리스트 필터링 제공(아카이빙/탐색성 강화)

## 1.2 범위(최소)

### **Public**

* 멤버 리스트 조회(필터 지원)
* 멤버 상세 조회

### **Protected**

* 내 멤버 프로필 조회
* 내 멤버 프로필 업서트(생성/수정 통합)

---

# 2. 아키텍처/정책 (변경 없음)

## 2.1 인증(Authentication)

* 클라이언트에서 Firebase Auth로 로그인 수행
* 서버 요청 시 `Authorization: Bearer <Firebase ID Token>` 전송
* 서버는 Firebase Admin SDK로 토큰 검증 후 `firebaseUid`를 획득

## 2.2 인가(Authorization)

* `/me/**` 는 로그인 필수
* Public API(`/members/**`)는 누구나 접근 가능
* “소유자 검사(owner check)”는 엔드포인트를 `/me/member`로 제한하여 제거(항상 본인 데이터만 변경)

## 2.3 데이터 원칙

* 사용자 식별은 `firebase_uid`를 단일 기준으로 사용
* 링크/카드/스킬은 JSON 컬럼(또는 TEXT JSON)으로 저장(테이블 분해 생략)

---

# 3. User Flow(행동 흐름) (업데이트)

## 3.1 랜딩 → 멤버 리스트

* 사용자: 랜딩에서 멤버 리스트로 이동
* 서버: `GET /members` 호출로 리스트 조회
* (선택 B) 사용자: 기수/파트 필터 선택 → `GET /members?generation=5&part=BE` 등으로 재조회

## 3.2 로그인 → 프로필 등록/수정

* 사용자: 구글 로그인(Firebase)
* 클라이언트: Firebase ID Token 획득
* 사용자: 프로필 등록/수정 페이지 진입 시 기존 데이터 로드
* 서버: `GET /me/member` 호출(없으면 404 또는 빈 응답 처리)
* 사용자: 저장
* 서버: `PUT /me/member` 호출(없으면 생성, 있으면 수정)

## 3.3 멤버 상세 조회

* 사용자: 멤버 리스트에서 특정 멤버 선택
* 서버: `GET /members/{publicId}` 호출

---

# 4. API 명세(최소안) (업데이트)

## 4.1 공통 규칙

* Protected API 요청 헤더: `Authorization: Bearer <FIREBASE_ID_TOKEN>`
* Content-Type: `application/json`
* 응답 에러 포맷(공통):

```json
{
  "code": "STRING_CODE",
  "message": "Human readable message"
}
```

---

## 4.2 멤버 리스트 조회 (Public)

### `GET /members`

**설명**

* 멤버 리스트 페이지용 “요약 정보” 조회
* (선택 B) 기수/파트 필터 지원

**Query (선택)**

* `generation`: 숫자 또는 문자열(예: `5`)
* `part`: enum(예: `BE`, `FE`, `APP`, `DE`, `AI` 등 팀 기준)
* `level`: enum(기존 유지)
* `position`: enum(기존 유지)
* `q`: 이름 검색(선택, 단순 contains; 정말 최소면 생략 가능)

**Response 200**

```json
{
  "items": [
    {
      "publicId": "uuid",
      "name": "String",
      "image": "https://... (optional)",
      "generation": 5,
      "part": "BE",
      "level": "CORE",
      "position": "BE"
    }
  ]
}
```

**비고**

* 성능을 위해 links/cards/about 등 상세 필드는 제외

---

## 4.3 멤버 상세 조회 (Public)

### `GET /members/{publicId}`

**설명**

* 멤버 공개 프로필 상세 조회

**Path**

* `publicId`: UUID

**Response 200**

```json
{
  "publicId": "uuid",
  "name": "String",
  "image": "https://... (optional)",
  "bio": "optional",
  "about": "optional",
  "generation": 5,
  "part": "BE",
  "level": "CORE",
  "position": "BE",
  "skills": ["optional", "list"],
  "links": [
    { "icon": "GITHUB", "url": "https://..." }
  ],
  "cards": [
    { "title": "String", "description": "optional", "url": "optional" }
  ]
}
```

**Response 404**

```json
{ "code": "MEMBER_NOT_FOUND", "message": "Member not found." }
```

---

## 4.4 내 멤버 프로필 조회 (Protected) ✅ 추가

### `GET /me/member`

**설명**

* 로그인한 사용자의 “내 프로필” 조회(수정 화면 진입 시 사용)

**인증**

* 필요(Firebase ID Token)

**Response 200**

```json
{
  "publicId": "uuid",
  "name": "String",
  "image": "https://... (optional)",
  "bio": "optional",
  "about": "optional",
  "generation": 5,
  "part": "BE",
  "level": "CORE",
  "position": "BE",
  "skills": ["optional", "list"],
  "links": [
    { "icon": "GITHUB", "url": "https://..." }
  ],
  "cards": [
    { "title": "String", "description": "optional", "url": "optional" }
  ]
}
```

**Response 404**

```json
{ "code": "PROFILE_NOT_FOUND", "message": "Profile not found." }
```

**Response 401**

```json
{ "code": "UNAUTHORIZED", "message": "Invalid or missing token." }
```

---

## 4.5 내 멤버 프로필 업서트(생성/수정 통합) (Protected) (업데이트)

### `PUT /me/member`

**설명**

* 로그인한 사용자의 멤버 프로필을 없으면 생성 / 있으면 수정(Upsert)
* 단순화를 위해 전체 객체 전송(부분 PATCH 미지원)

**인증**

* 필요(Firebase ID Token)

**Request**

```json
{
  "name": "String",
  "image": "https://... (optional)",
  "bio": "optional",
  "about": "optional",
  "generation": 5,
  "part": "BE",
  "level": "CORE",
  "position": "BE",
  "skills": ["optional", "list"],
  "links": [
    { "icon": "GITHUB", "url": "https://..." }
  ],
  "cards": [
    { "title": "String", "description": "optional", "url": "optional" }
  ]
}
```

**Response 200**

```json
{ "publicId": "uuid" }
```

**서버 처리 규칙**

* 토큰 검증 후 `firebaseUid` 추출
* `members.firebase_uid = firebaseUid`로 조회

    * 없으면: 새 레코드 생성(`public_id` UUID 생성)
    * 있으면: 입력값으로 덮어쓰기 업데이트
* 필수: `name, generation, part` (그리고 기존 유지가 필요하면 `level, position`도 필수로 유지)

**Response 400**

```json
{ "code": "VALIDATION_ERROR", "message": "name/generation/part is required." }
```

**Response 401**

```json
{ "code": "UNAUTHORIZED", "message": "Invalid or missing token." }
```

---

# 5. 데이터 스키마(최소) (업데이트)

## 5.1 Enum 정의

### Level

* `ORG | CORE | NEW | ALU`

### Position

* `APP | FE | BE | DE | AI`

### Part ✅ 추가(아카이빙/파트 분류용)

* `APP | FE | BE | DE | AI | DESIGN | PM`

    * 팀에서 쓰는 실제 파트명이 다르면 여기만 팀 기준으로 확정

### Icon

* `GITHUB | FIGMA | DRIBBBLE | INSTAGRAM | MAIL | LINK`

## 5.2 members 테이블(서버 저장) (업데이트)

* `id` (PK)
* `public_id` (UUID, UNIQUE, NOT NULL)
* `firebase_uid` (UNIQUE, NOT NULL)
* `name` (NOT NULL)
* `image` (NULL)
* `bio` (NULL)
* `about` (NULL)
* `generation` (INT or VARCHAR, NOT NULL) ✅ 추가
* `part` (VARCHAR, NOT NULL) ✅ 추가
* `level` (NOT NULL)
* `position` (NOT NULL)
* `skills_json` (NULL)
* `links_json` (NULL)
* `cards_json` (NULL)
* `created_at`, `updated_at` (NOT NULL)

**인덱스**

* UNIQUE(`firebase_uid`)
* UNIQUE(`public_id`)
* INDEX(`generation`, `part`) ✅ (선택 B 필터 성능용, 가볍게 추가)

---

# 6. 미들웨어(최소) (업데이트)

## 6.1 Firebase 인증 필터

* 보호 경로: `/me/**`
* 동작:

    1. Authorization 헤더에서 Bearer 토큰 추출
    2. Firebase Admin SDK로 토큰 검증
    3. `firebaseUid`를 요청 컨텍스트에 저장
    4. 실패 시 401 반환

---

# 7. 입력 검증(최소 규칙) (업데이트)

* `name`: 필수, 공백 불가
* `generation`: 필수, 숫자/정해진 형식만 허용(최소안은 숫자 권장)
* `part`: 필수, enum 값만 허용
* `level`: 필수, enum 값만 허용
* `position`: 필수, enum 값만 허용
* `links[].icon`: enum 값만 허용
* `cards[].title`: 카드가 있으면 title 필수

---

# 9. 구현 체크리스트(최종) (업데이트)

* [ ] Firebase Admin SDK 초기화(서비스 계정)
* [ ] 인증 필터(`/me/**`)
* [ ] `GET /members` (+ generation/part 필터 처리)
* [ ] `GET /members/{publicId}`
* [ ] `GET /me/member` ✅ 추가
* [ ] `PUT /me/member` 업서트(+ generation/part 반영)
* [ ] members 테이블 + UNIQUE 인덱스 2개 + (선택) INDEX(generation, part)