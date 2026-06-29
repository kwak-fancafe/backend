# FanCafe

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

크리에이터와 팬이 소통하는 **팬 커뮤니티 플랫폼** 백엔드 서버입니다.
실무 수준의 DDD 설계와 계층 분리, 다양한 도메인 규칙을 직접 구현하는 포트폴리오 프로젝트입니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Database | MySQL 8.x (prod), H2 (test) |
| Auth | JWT (jjwt 0.12.3) |
| Storage | AWS S3 (첨부파일) |
| API Docs | Springdoc OpenAPI 3 (Swagger UI) |
| Build | Gradle |

---

## 아키텍처

**DDD 계층형 모놀리스** — Presentation → Application → Domain / Infrastructure 단방향 의존.

### 바운디드 컨텍스트

```
┌──────────────────────────────────────────────────────────┐
│  Member BC          Community BC           Admin BC       │
│  ─────────────      ───────────────────   ─────────────  │
│  Member             Category              VisitorStats   │
│  LoginId VO         Post + Attachment                    │
│  Password VO        Comment (계층형)                      │
│  Nickname VO        Like (다형성)                         │
│                     ViewCount                            │
└──────────────────────────────────────────────────────────┘
```

### 서비스 분리 원칙

| 타입 | 역할 | 예시 |
|------|------|------|
| `XxxCommandService` | 기본 생명주기 (create, update, delete) | `PostCommandService` |
| `XxxYyyService` | 독립적인 도메인 행위 (상태 전환, 도메인 규칙 중심) | `PostDraftService`, `MemberBanService` |
| `XxxQueryService` | 읽기 전체 (`@Transactional(readOnly=true)`) | `PostQueryService` |
| `XxxFacade` | BC 간 조율 (여러 서비스 + Member BC 등 합산) | `PostFacade` |


---

## 패키지 구조

```
src/main/java/org/kwakmunsu/fancafe/
├── global/
│   ├── config/                 # Security, JPA 설정
│   ├── infrastructure/
│   │   ├── jwt/                # JWT 필터·프로바이더
│   │   └── s3/                 # AWS S3 업로드
│   └── support/
│       ├── BaseEntity.java     # JPA Audit (createdAt/updatedAt/deletedAt)
│       ├── EntityStatus.java   # ACTIVE | DELETED
│       ├── error/              # CoreException, ErrorType, 글로벌 핸들러
│       ├── logging/            # MDC, 요청 로깅
│       └── response/           # ApiResponse<T> 래퍼
├── member/                     # 회원 도메인
├── auth/                       # 인증·인가 (JWT 재발급 등)
├── community/
│   ├── category/               # 카테고리 CRUD
│   ├── post/                   # 게시글 + 첨부파일
│   ├── comment/                # 댓글 + 대댓글
│   ├── like/                   # 게시글·댓글 좋아요
│   └── viewcount/              # 조회수
└── admin/                      # 어드민 대시보드
```

---

## 주요 기능

### 인증·회원
- 회원가입 / 로그인 / 로그아웃 / 회원 탈퇴
- JWT Access + Refresh Token
- 역할 기반 접근 제어: `CREATOR` / `MANAGER` / `FAN`

### 커뮤니티
- 카테고리 CRUD + 순서 변경 (CREATOR 전용)
- 게시글 작성·임시저장·발행·수정·삭제 (첨부파일 최대 5개, 10MB)
- 댓글·대댓글 (Soft Delete — 하위 댓글이 있으면 placeholder 유지)
- 게시글·댓글 좋아요 토글

### 조회수
- 24시간 내 동일 사용자 중복 카운트 방지

### 방문자 통계
- 일일 방문자 수 집계 및 누적 통계

### 어드민
- 회원 정지·해제, 강제 게시물·댓글 삭제, 역할 변경
- 방문자 통계 대시보드

---

## 빌드 & 실행

```bash
# 빌드
./gradlew build

# 로컬 실행 (H2 인메모리 DB)
./gradlew bootRun

# 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests "org.kwakmunsu.fancafe.member.domain.MemberTest"
```

### 프로파일

| 프로파일 | 용도 |
|----------|------|
| `local` | 로컬 개발 (H2, 기본값) |
| `dev` | 개발 서버 (MySQL) |
| `prod` | 운영 서버 (MySQL, 로그 최소화) |
| `test` | 통합 테스트 (H2) |

---

## API 문서

서버 실행 후 Swagger UI 접근:

```
http://localhost:8080/swagger-ui/index.html
```

### 엔드포인트 요약

| 리소스 | 메서드 | 경로 | 권한 |
|--------|--------|------|------|
| 회원가입 | POST | `/api/v1/auth/register` | 누구나 |
| 로그인 | POST | `/api/v1/auth/login` | 누구나 |
| 게시글 목록 | GET | `/api/v1/posts` | 누구나 |
| 게시글 작성 | POST | `/api/v1/posts` | FAN+ |
| 댓글 작성 | POST | `/api/v1/posts/{postId}/comments` | FAN+ |
| 카테고리 생성 | POST | `/api/v1/categories` | CREATOR |
| 어드민 대시보드 | GET | `/api/v1/admin/stats` | CREATOR |

> 전체 API 명세는 [`/docs/API.md`](./docs/API.md) 참조

---

## 문서

| 파일 | 내용 |
|------|------|
| [`docs/DOMAIN_MODEL.md`](./docs/DOMAIN_MODEL.md) | 도메인 모델, 애그리거트, VO, 상태 전이 |
| [`docs/SRS.md`](./docs/SRS.md) | 기능·비기능 요구사항, 구현 로드맵 |
| [`docs/API.md`](./docs/API.md) | REST API 전체 엔드포인트 명세 |
| [`docs/ERD.md`](./docs/ERD.md) | DB 스키마, 인덱스, 설계 결정 사항 |
| [`docs/PACKAGE_STRUCTURE.md`](./docs/PACKAGE_STRUCTURE.md) | 패키지 구조 상세, DTO 흐름 |
| [`docs/CONVENTION.md`](./docs/CONVENTION.md) | 코딩 컨벤션, Git 커밋 규칙 |
