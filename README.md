# 🔒 Spring Security & JWT 실습 프로젝트 (`sec-jwt`)

본 프로젝트는 Spring Boot, Spring Security, JPA, Thymeleaf를 활용하여 **무상태(Stateless) 기반의 회원가입, JWT 로그인, 쿠키 기반 사용자 인증**을 구현한 실습 프로젝트입니다.

---

## 📅 오늘 실습한 핵심 내용 정리

오늘 진행한 실습 내용은 크게 **환경 설정 분리, 보안 기초 설계, JWT 기반의 로그인/인증 처리, 그리고 Thymeleaf를 화면 연동**으로 구성됩니다.

### 1. 환경 설정 및 프로파일 분리 ⚙️
- **환경 변수 로드 (`.env`):** DB 주소, 패스워드, JWT 비밀키 등 민감 정보를 소스코드에 직접 노출하지 않고 `.env` 파일에서 주입받도록 구성했습니다.
- **설정 파일 분리:** 유지보수를 위해 프로파일별로 설정을 나눴습니다.
  - `application.yaml`: 기본 설정 및 활성 프로파일(dev, db, auth) 정의
  - `application-db.yaml`: PostgreSQL 연결 정보 및 JPA/Hibernate 설정
  - `application-auth.yaml`: Argon2 암호화 강도 및 JWT 만료시간, 비밀키 설정

### 2. JPA Auditing & Base Entity 💾
- **자동 시간 기록:** 데이터 생성일과 수정일을 자동으로 관리하기 위해 `@CreatedDate` 및 `@LastModifiedDate`가 포함된 `BaseEntity` 클래스를 정의했습니다.
- **Auditing 활성화:** `@EnableJpaAuditing` 설정을 위해 `JpaConfig` 클래스를 분리하여 선언했습니다.

### 3. 강화된 패스워드 암호화 (`PasswordEncoder`) 🔐
- **BouncyCastle 라이브러리 연동:** 높은 수준의 패스워드 보안 알고리즘을 지원하도록 라이브러리를 추가했습니다.
- **다중 암호화 지원 (`DelegatingPasswordEncoder`):** `argon2`, `scrypt`, `bcrypt` 등 다양한 해시 알고리즘을 지원하며, 기본 암호화 알고리즘으로 강력한 **Argon2**를 설정했습니다.

### 4. 회원가입 및 DB 연동 👤
- 사용자 계정 정보를 저장할 `UserAccountEntity` 및 `UserAccountJpaRepository`를 생성했습니다.
- 가입 요청 시 DTO(`UserAccountSignUpDTO`)를 통해 데이터를 안전하게 받고, 패스워드를 해시화한 뒤 데이터베이스에 저장하도록 구현했습니다.

### 5. JWT 기반 로그인 및 무상태(Stateless) 인증 🔑
- **세션 무사용 설정:** 서버 세션을 사용하지 않도록 Spring Security를 `SessionCreationPolicy.STATELESS`로 설정했습니다.
- **JWT 토큰 생성 및 검증 (`JwtProvider`):** `jjwt` 라이브러리를 사용하여 사용자의 아이디를 기반으로 Access Token을 생성하고, 유효성을 검증하며, Claims를 파싱하여 사용자 아이디를 추출하는 유틸리티를 작성했습니다.
- **보안 쿠키 발급 (`AuthCookieUtil`):** 로그인 성공 시 생성된 JWT를 브라우저의 일반 로컬스토리지 대신, XSS 공격에 안전한 **HttpOnly 쿠키**에 담아 발급하도록 처리했습니다. (쿠키 경로: `/`)
- **인증 필터 구현 및 등록 (`JwtFilter`):**
  - `OncePerRequestFilter`를 상속받은 커스텀 필터입니다.
  - 매 요청마다 쿠키에서 JWT를 읽어 유효성을 검증하고, 유효한 토큰일 경우 `UsernamePasswordAuthenticationToken`을 생성하여 Security Context(`SecurityContextHolder`)에 저장해 사용자 인증을 인가합니다.

### 6. Thymeleaf 뷰 템플릿 및 컨트롤러 연동 🖥️
- **Thymeleaf 연동:** 실제 웹 브라우저에서 동작 흐름을 시각적으로 테스트할 수 있도록 간단한 화면 뷰를 연동했습니다.
  - `index.html`: 비로그인 사용자도 접근 가능한 메인 페이지 (회원가입/로그인 기능 제공)
  - `my.html`: 인증을 거친 사용자만 접근할 수 있는 마이페이지 (사용자 아이디 표시 및 로그아웃 가능)
- **컨트롤러 처리 (`MyController`):** 로그인 성공 후 메인 페이지에서 쿠키를 지닌 채 마이페이지(`/my`)로 리다이렉트되어 사용자 정보를 렌더링하도록 흐름을 잡았습니다.

---

## 🛠️ 동작 방식 요약 (로그인 & 인증 흐름)

```mermaid
sequenceDiagram
    actor User as 사용자 (브라우저)
    participant Server as 서버 (Spring Boot)
    database DB as 데이터베이스

    Note over User, Server: 1. 로그인 요청
    User->>Server: POST /api/v1/users/login (id, password)
    Server->>DB: 사용자 정보 조회 및 비밀번호(Argon2) 비교
    DB-->>Server: 일치 확인
    Server->>Server: JwtProvider로 JWT Access Token 생성
    Server-->>User: HttpOnly 쿠키(path: '/')에 JWT를 담아 응답

    Note over User, Server: 2. 마이페이지 접근 (인증 필요)
    User->>Server: GET /my (요청 헤더 쿠키에 JWT 자동 포함)
    Server->>Server: JwtFilter 동작 (쿠키의 JWT 추출 및 유효성 검증)
    Server->>Server: 검증 완료 후 SecurityContextHolder에 인증 객체 저장
    Server->>Server: MyController가 인증 객체에서 사용자 정보 추출
    Server-->>User: my.html 페이지 렌더링 (사용자 아이디 노출)
```

## 🚀 프로젝트 실행 방법

1. **`.env` 파일 작성:**
   - 프로젝트 루트 디렉토리에 `.env` 파일을 생성하고 아래 형식으로 채웁니다.
   ```properties
   PGHOST=your_db_host
   PGDATABASE=your_db_name
   PGUSER=your_db_user
   PGPASSWORD=your_db_password
   JWT_SECRET=your_base64_encoded_jwt_secret_key_minimum_256bits
   ```

2. **애플리케이션 빌드 및 실행:**
   ```bash
   ./gradlew bootRun
   ```

3. **브라우저 접속:**
   - 메인 화면: `http://localhost:8080/`
   - API 명세서 (Swagger): `http://localhost:8080/swagger-ui/index.html`
