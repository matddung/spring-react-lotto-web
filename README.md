# LottoWeb Project

-----
![lotto-web-600x600](https://github.com/user-attachments/assets/1e5bd5b6-2524-4fb0-af2c-beec081230b4)

## 📌 Overview

-----
로또 번호를 추천해주는 서비스를 제공하는 플랫폼입니다. (현재는 인프라 비용 및 리팩토링 작업으로 인해 운영은 중단된 상태입니다.)

-----
## ⚡ Quick Start
로컬 실행은 **Backend → Frontend 순서**를 권장합니다.

### Backend

1. 서버 실행

```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=local'
```

2실행 확인
    - 기본 주소: `http://localhost:8080`
    - Swagger: `http://localhost:8080/swagger-ui/index.html`

### Frontend

1. 의존성 설치

```bash
cd frontend
npm install
```

2. 개발 서버 실행

```bash
npm start
```

3. 실행 확인
    - 기본 주소: `http://localhost:3000`
    - 프론트 기본 API 주소는 `frontend/src/constants/index.js`의 `API_BASE_URL`(`http://localhost:8080`)을 사용합니다.

---

### API 응답 규격

원칙: **성공은 `ApiResponse`로 래핑하고, 실패는 `ErrorResponse`를 그대로 반환합니다.**

>정상 응답은 HTTP 200 범위에서 반환되며, 실패는 HTTP 상태 코드와 함께 ErrorResponse가 반환됩니다.

- 성공 예시 (`ApiResponse`)

```json
{
  "check": true,
  "data": {
    "accessToken": "<JWT>",
    "refreshToken": "<JWT>",
    "tokenType": "Bearer"
  }
}
```

- 실패 예시 (`ErrorResponse`)

```json
{
  "success": false,
  "code": "AUTH_401",
  "message": "Full authentication is required to access this resource",
  "path": "/api/user",
  "timestamp": "2025-01-01T12:34:56"
}
```

---

### 주요 기능

- Weka Library를 사용하여 과거 당첨 번호를 분석하고, 다양한 머신러닝 알고리즘을 통해 5개의 예측 번호를 도출합니다.
- 아이디당 일주일에 한 번만 예측 번호를 받아볼 수 있도록 하고, 스케줄링을 통해 발급된 내역을 일주일에 한번 삭제하도록 하였습니다.
- 다양한 클라이언트에서 접근할 수 있도록 RESTful API 설계·구현하였습니다.
- 사용자의 편의를 위해 소셜 로그인을 구현하여 쉽게 회원 가입 및 로그인이 가능하도록 하였습니다.
- Jenkins와 Docker, docker-compose를 통해 자동으로 빌드 및 배포가 진행되도록 설정하였습니다.

----

### ✍ Achieved

#### 1. API/아키텍처 정리
- API 응답을 성공(ApiResponse) / 실패(ErrorResponse) 단일 포맷으로 통일
- GlobalExceptionHandler 도입으로 예외 응답 표준화
#### 2. 테스트 기반 강화
- MockMvc 기반 Controller 테스트로 응답 규격 회귀 테스트 고정
- Service 단위 테스트로 핵심 비즈니스 로직 검증
- 총 13개 테스트 클래스, 59개 테스트 케이스
#### 3. 테스트로 발견한 결함 수정
- JWT 필터의 refresh 예외 경로 수정(`/user/refresh` → `/api/user/refresh`)
- 전용 단위 테스트 추가
#### 4. 실행 환경 분리
- DB 예약어(user) 충돌 해결 → users 변경
- local(H2) / 운영(MySQL) 환경 분리

---

### 🚀 Backend Skills

---
![시스템 아키텍처 drawio](https://github.com/user-attachments/assets/fbb27a26-79ff-4812-a760-082a806fd9cb)
### Core
- Spring Boot
- JPA
- Spring Security
- JWT
### Infra
- Docker
- Jenkins
- Nginx
### Optional / Experimental
- Prometheus
- Weka
- Ngrok

----

#### 기술적 의사 결정

>1. 로드밸런서, 타겟 그룹, ACM vs Nginx, CertBot
>- AWS 로드밸런서와 ACM을 우선적으로 고려했으나, 프로젝트의 규모와 비용 효율성을 감안했을 때 Nginx와 CertBot이 더 적합하다고 판단했습니다. Nginx는 리버스 프록시 서버로서 유연성이 좋고, 다양한 설정을 통해 안정성을 높일 수 있습니다. CertBot은 무료 SSL 인증서를 발급받고 자동 갱신할 수 있어 관리가 용이합니다.

>2. Docker, docker-compose, Jenkins를 사용한 이유
>- 애플리케이션의 환경 일관성을 보장하고, 이식성을 향상시키기 위해서입니다. Docker-compose는 복잡한 멀티 컨테이너 애플리케이션을 쉽게 정의하고 실행할 수 있게 해주며, Jenkins는 CI/CD 파이프라인을 통해 자동화된 빌드, 테스트, 배포를 가능하게 합니다.

>3. WSL로 로컬 환경을 리눅스 환경으로 만든 이유
>- Windows 환경보다 Linux 환경에서 Jenkins가 더 안정적이고 성능이 뛰어나기 때문입니다. Jenkins는 Linux 기반 서버에서 더 원활하게 동작하며, 리소스 관리와 스크립트 자동화에서 많은 이점을 제공합니다. WSL을 사용하면 로컬에서 Linux 환경을 손쉽게 설정할 수 있어, 개발 환경과 실제 배포 환경을 일치시켜 호환성 문제를 줄이고, 개발 효율성을 높일 수 있습니다.

----

## 🔐 JWT 인증 흐름

### 1) 로그인 요청/응답 규격

#### 로그인 요청
- **URL**: `POST /api/user/signIn`
- **Body(JSON)**

```json
{
  "email": "string@aa.bb",
  "password": "string"
}
```

- 유효성:
    - `email`: 필수, 이메일 형식
    - `password`: 필수, 8~50자

#### 로그인 성공 응답
- 모든 정상 응답은 공통 래퍼(`ApiResponse`)로 감싸서 내려옵니다.
- 클라이언트 요청 헤더 예시:
    - `Authorization: Bearer <accessToken>`

#### 토큰 재발급 요청/응답
- **URL**: `POST /api/user/refresh`
- **Body(JSON)**

```json
{
  "refreshToken": "<JWT>"
}
```

- 성공 시 응답(`data`)에 신규 `accessToken`이 내려오고,
    - refresh token 만료 전이면 기존 refresh token 유지
    - refresh token 만료 후 재발급 로직이면 refresh token도 교체

#### 에러 응답 규격
- 인증/비즈니스/검증 실패 시 `ErrorResponse` 형태로 응답합니다.
- 공통 필드:
    - `success`: 항상 `false`
    - `code`: 에러 코드 (`AUTH_401`, `AUTH_403`, `COMMON_400` 등)
    - `message`: 사용자 안내 메시지
    - `path`: 요청 URI
    - `timestamp`: 에러 발생 시각
  

- 참고: 정상 응답(`ApiResponse`)과 달리 에러는 `check/data` 래퍼 없이 `ErrorResponse` 본문으로 내려옵니다.

---

### 2) 토큰 필요 API / 불필요 API

`SecurityConfig` 기준으로 `ALLOWED_URIS`만 비인증 접근 가능이며, 그 외는 기본적으로 인증 필요(`anyRequest().authenticated()`)입니다.

#### 토큰 불필요(permitAll)
- `POST /api/user/signUp`
- `POST /api/user/signIn`
- `POST /api/user/find-password`
- `POST /api/user/refresh`
- `/oauth2/**`, `/login/**` (소셜 로그인 시작/콜백)
- Swagger, 정적 리소스, `/actuator/prometheus` 등

#### 토큰 필요(대표 API)
- 유저: `GET /api/user`, `DELETE /api/user`, `PUT /api/user/password`, `PUT /api/user/nickname`, `POST /api/user/signOut`
- 로또: `/api/lotto/*` 전체 (`top6`, `pattern-recognition`, `random`, `ensemble`, `monte-carlo`, `user-lotto-info`)
- 질문: `POST /api/question/create`, `GET /api/question/my-list`, `GET /api/question/detail`, `POST /api/question/answer`
- 관리자: `/api/admin/*` 전체

#### 참고(토큰 없이 가능한 질문 API)
- `GET /api/question/list`는 컨트롤러 파라미터에 `@CurrentUser`가 없어 비회원 목록 조회 용도로 동작합니다.

---
### Troubleshooting

- [Ngrok을 이용한 Webhook 설정 및 로컬 서버 공개](https://velog.io/@studyjun/Ngrok%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-Webhook-%EC%84%A4%EC%A0%95-%EB%B0%8F-%EB%A1%9C%EC%BB%AC-%EC%84%9C%EB%B2%84-%EA%B3%B5%EA%B0%9C)
- [배포 환경에서 OAuth2 소셜 로그인을 위한 Nginx 설정 문제 해결](https://velog.io/@studyjun/%EB%B0%B0%ED%8F%AC-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-OAuth2-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%9D%84-%EC%9C%84%ED%95%9C-Nginx-%EC%84%A4%EC%A0%95-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
- [Windows 환경에서 발생하는 Jenkins 에러를 Linux 환경으로 전환하여 해결](https://velog.io/@studyjun/Windows-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%EB%B0%9C%EC%83%9D%ED%95%98%EB%8A%94-Jenkins-%EC%97%90%EB%9F%AC%EB%A5%BC-Linux-%ED%99%98%EA%B2%BD%EC%9C%BC%EB%A1%9C-%EC%A0%84%ED%99%98%ED%95%98%EC%97%AC-%ED%95%B4%EA%B2%B0)
- [Weka 데이터 분석 모델의 패턴 인식 모델 오류 해결](https://velog.io/@studyjun/Weka-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%B6%84%EC%84%9D-%EB%AA%A8%EB%8D%B8%EC%9D%98-%ED%8C%A8%ED%84%B4-%EC%9D%B8%EC%8B%9D-%EB%AA%A8%EB%8D%B8-%EC%98%A4%EB%A5%98-%ED%95%B4%EA%B2%B0)
- [EC2 인스턴스 용량 문제 해결](https://velog.io/@studyjun/EC2-%EC%9D%B8%EC%8A%A4%ED%84%B4%EC%8A%A4-%EC%9A%A9%EB%9F%89-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
- [Jenkins 빌드 시 DB 메모리 초기화 문제](https://velog.io/@studyjun/Jenkins-%EB%B9%8C%EB%93%9C-%EC%8B%9C-DB-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%B4%88%EA%B8%B0%ED%99%94-%EB%AC%B8%EC%A0%9C)
- [Google 소셜 로그인 진행 시 닉네임 설정 문제](https://velog.io/@studyjun/Google-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%A7%84%ED%96%89-%EC%8B%9C-%EB%8B%89%EB%84%A4%EC%9E%84-%EC%84%A4%EC%A0%95-%EB%AC%B8%EC%A0%9C)

------
### Improvement Backlog

- [~~Jenkins 느린 빌드 시간 문제~~](https://velog.io/@studyjun/Jenkins-%EB%8A%90%EB%A6%B0-%EB%B9%8C%EB%93%9C-%EC%8B%9C%EA%B0%84-%EB%AC%B8%EC%A0%9C)
- [~~SSL 인증서 만료 시 자동 갱신 문제~~](https://velog.io/@studyjun/SSL-%EC%9D%B8%EC%A6%9D%EC%84%9C-%EB%A7%8C%EB%A3%8C-%EC%8B%9C-%EC%9E%90%EB%8F%99-%EA%B0%B1%EC%8B%A0-%EB%AC%B8%EC%A0%9C)
- [~~Prometheus + grafana + Alertmanager 모니터링, 시각화 및 알림~~](https://velog.io/@studyjun/Prometheus-Grafana-Alertmanager-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%8B%9C%EA%B0%81%ED%99%94-%EB%B0%8F-%EC%95%8C%EB%A6%BC)

------

#### ERD
![lottoERD](https://github.com/user-attachments/assets/1ba9e515-e12a-4d35-abd8-dfe393653d1f)

#### FileTree

```
lottoweb
├─ 📦frontend
│  ├─ 📜Dockerfile
│  └─ 📂src
│     ├─ 📂app
│     ├─ 📂common
│     ├─ 📂constants
│     ├─ 📂home
│     ├─ 📂img
│     ├─ 📂question
│     ├─ 📂user
│     └─ 📂util
├─ 📦backend
│  ├─ 📜Dockerfile
│  └─ 📂src
│     ├─ 📂main
│     │  ├─ 📂java
│     │  │  └─ 📂lottoweb
│     │  │     ├─ 📂config
│     │  │     ├─ 📂controller
│     │  │     ├─ 📂dto
│     │  │     ├─ 📂entity
│     │  │     ├─ 📂exception
│     │  │     ├─ 📂handler
│     │  │     ├─ 📜LottowebApplication.java
│     │  │     ├─ 📂repository
│     │  │     ├─ 📂security
│     │  │     ├─ 📂service
│     │  │     └─ 📂util
│     │  └─ 📂resources
│     └─ 📂test
│        ├─📂controller
│        ├─📂exception
│        └─📂service
├─ 📜docker-compose.yml
└─ 📜Jenkinsfile
```
