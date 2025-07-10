# 📘 프로젝트 관리 규칙 정리

본 문서는 프로젝트의 Git 브랜치 전략, 디렉터리 구조, 코드 컨벤션, PR 규칙, ERD 작성 방식 등을 정리한 페이지입니다.  
팀원 간 일관된 개발 문화를 위해 필수적으로 참고해주세요.

---

## 📌 Git 브랜치 전략

| 브랜치 이름          | 설명                                           |
|----------------------|------------------------------------------------|
| `master`             | **배포용 브랜치**                              |
| `dev`                | **개발 중인 브랜치**                           |
| `이름/feat/기능`     | **기능 개발 브랜치** 예: `jun/feat/login`      |
| `fix/버그설명`       | **버그 수정 브랜치** 예: `fix/login-error`     |
| `refactor/간략설명`  | **리팩토링 브랜치** (기능 변경 없음)          |

---

## 🧾 프로젝트 디렉터리 구조

<pre><code>com.phone.project
├── apiPayload
│   ├── code
│   └── exception
├── domain
│   ├── entity
│   └── enum
├── service
├── web
│   ├── controller
│   └── dto
│       ├── request
│       └── response
├── config
├── exception
└── repository
</code></pre>

## ✒️ 코드 컨벤션 정리

### 1. 주석 규칙 (공유되는 코드 중심)

<pre><code>
/**
 * 사용자 정보를 조회합니다.
 * @param userId 사용자 ID
 * @return 사용자 정보
 */
</code></pre>

### 2. 네이밍 컨벤션
<b>📁 DB<b/>
| 항목  | 케이스          | 예시           |
| --- | ------------ | ------------ |
| 테이블 | `snake_case` | `user_table` |
| 컬럼명 | `snake_case` | `created_at` |

<br>

<b>💡 기타 규칙</b>
| 항목        | 케이스                | 예시               |
| --------- | ------------------ | ---------------- |
| 상수 / enum | `UPPER_SNAKE_CASE` | `TEACHER_STATUS` |
| 변수        | `camelCase`        | `userId`         |
| 클래스       | `PascalCase`       | `UserService`    |
| 메서드       | `camelCase`        | `getUserInfo()`  |

---
## 🔁 PR 규칙 정리
- 기능마다 Issue를 생성하고, 해당 이슈 내에서 브랜치 작업 후 PR 생성
- PR은 최소 1명 이상의 코드 리뷰 필수

## ✅ PR 제목 예시
- feat: 사용자 로그인 기능 추가
- fix: 비밀번호 검증 오류 수정
---

## 🧩 ERD 정리
- 작성 툴: ERDCloud
- 작성 완료 후 /docs/ERD 디렉토리에 ERD 이미지 또는 링크 저장 예정

