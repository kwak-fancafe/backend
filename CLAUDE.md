# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Current State

글로벌 인프라(에러 핸들링·응답 래퍼·로깅·Base Entity)와 모든 BC 스캐폴딩 완료.
테스트 지원 클래스(`UnitTestSupport`, `IntegrationTestSupport`, `ControllerTestSupport`) 추가됨.
환경별 `application.yml` 파일 존재 (local·dev·prod·test).

**루트 패키지**: `org.kwakmunsu.fancafe` (docs의 `com.fancafe`는 오기)

**`build.gradle`에 아직 없는 의존성** — 구현 전 추가 필요:
`spring-boot-starter-data-redis`, QueryDSL, `spring-boot-starter-batch`, `org.jsoup:jsoup`, AWS S3 SDK

## Build & Run

```bash
./gradlew build / bootRun / test
./gradlew test --tests "org.kwakmunsu.fancafe.member.domain.MemberTest"
```

## Architecture

DDD 계층형 모놀리스. 바운디드 컨텍스트: **Auth** / **Member** / **Community** (Category·Post·Comment·Like·ViewCount) / **Admin**.

**레이어 금지 규칙:**
- `domain/` → application·presentation·infrastructure import 금지 (단, JPA 어노테이션·`CoreException`·`ErrorType` 허용 — 실용적 선택)
- `presentation/` → infrastructure 직접 import 금지 (application 경유)

**서비스 분리**: `XxxCommandService`(생명주기) / `XxxQueryService`(조회, 클래스 레벨 `@Transactional(readOnly=true)`) / `XxxYyyService`(도메인 행위) / `XxxFacade`(서비스 조합)

## 개발 원칙

**DDD**
- 비즈니스 규칙(검증, 상태 전이 조건)은 반드시 도메인 객체 내부에 위치. 서비스에 if-else로 흩어지면 안 된다.
- VO는 불변. 수정 시 새 인스턴스 반환. 컬렉션은 `List.copyOf()`로 방어적 복사.
- 서비스는 도메인 객체에 행위를 위임(`member.ban()`)하고 영속성을 담당. 상태를 꺼내 직접 판단하지 않는다.

**OOP**
- 접근 제한자 최소화 — `public`은 외부에서 실제로 호출하는 것만. 내부 검증 메서드는 `private`.
- 매직 넘버 금지 — 숫자·문자열 리터럴 대신 VO 상수 또는 Enum. (`> 20` 대신 `> Nickname.MAX_LENGTH`)
- 실행 단위별 개행 — 조회 → 검증 → 변환 → 저장 사이에 빈 줄로 구분.

## 구현 시 주의사항

비즈니스 규칙 상세는 `/docs/DOMAIN_MODEL.md`, `/docs/SRS.md` 참조. 아래는 docs에 없는 기술적 함정만 기재.

- **soft delete된 댓글에 `@SQLRestriction` 적용 금지** — 대댓글 있는 댓글은 `entityStatus=ACTIVE`를 유지한 채 `commentStatus=DELETED`로만 처리. `@SQLRestriction`이 걸리면 placeholder가 사라짐.
- **`likes` 엔티티에 `@Table(name = "likes")` 명시** — MySQL 예약어 충돌.
- **`post.content` 컬럼은 `MEDIUMTEXT`** — 50,000자 × UTF-8 = 200KB. `@Column(columnDefinition = "TEXT")`로 매핑 불가.

## 코드 규칙

- **Lombok**: `@RequiredArgsConstructor`(DI) + `@Getter`만. `@Setter`·`@Data`·`@Autowired` 금지. VO는 불변.
- **null 반환 금지**: `Optional` 또는 `throw new CoreException(ErrorCode.XYZ)`. 빈 컬렉션은 `List.of()`.
- **예외**: `CoreException` 단일 클래스. 에러 코드 형식 `{DOMAIN}_{SITUATION}`.
- **Soft delete 대상**: `Member`, `Category`, `Post`, `Comment`에만 `@SQLRestriction`. `Like`·`ViewCount`·`VisitorStats` 제외.
- **TDA**: 상태 전이는 도메인 메서드 내부. 서비스는 `member.ban()` 호출, 직접 setter 금지.

## 테스트

**기준**: 도메인 로직 검증 → 단위 테스트. 그 외 → 통합 테스트.

- **단위**: VO 검증, Entity 상태 전이, 도메인 메서드 (`Post.publish()`, `Member.ban()` 등). JUnit 5, `new`로 생성.
- **통합**: Service(트랜잭션·객체 협력), Repository(QueryDSL·`@SQLRestriction`), Controller(권한·`@Valid`). H2 + `@DataJpaTest` / `@WebMvcTest`.

**AssertJ 스타일 규칙**:
- 예외 메시지 검증: `.hasMessage(ErrorType.XXX.getMessage())` 사용
  ```java
  assertThatThrownBy(() -> new Nickname(null))
      .isInstanceOf(CoreException.class)
      .hasMessage(ErrorType.MEMBER_INVALID_NICKNAME.getMessage());
  ```
- 다중 필드 검증: `extracting` + 메서드 레퍼런스 + `containsExactly` 사용
  ```java
  assertThat(member).extracting(
      Member::getMemberStatus,
      Member::getEntityStatus,
      Member::getDeletedAt
  ).containsExactly(
      MemberStatus.WITHDRAWN,
      EntityStatus.DELETED,
      member.getDeletedAt()
  );
  ```

## 네이밍 & 컨벤션

- 테스트 메서드: 한국어 Given-When-Then — `회원가입_성공()`, `게시글_발행_실패_DRAFT_아닌_상태()`
- 커밋: `feat(member): 회원가입 API 구현`
- API base path: `/api/v1`, 응답: `ApiResponse<T>` 래퍼, 게시글 목록: 커서 페이지네이션

## Docs

`/docs/` 하위: `CONVENTION.md`, `DOMAIN_MODEL.md`, `PACKAGE_STRUCTURE.md`, `SRS.md`, `ERD.md`(설계 결정 사항 필독), `API.md`
