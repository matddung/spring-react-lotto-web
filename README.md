# LottoWeb Project

-----
![lotto-web-600x600](https://github.com/user-attachments/assets/1e5bd5b6-2524-4fb0-af2c-beec081230b4)

## 👆 Introduce

-----
로또 번호를 추천해주는 서비스를 제공하는 플랫폼입니다.

### 서비스 링크
[LottoWeb으로 이동](https://sbsmily.shop/)

- Test User ID : string@aa.bb
- Test User PW : string
- Test Admin ID : admin@admin.a
- Test Admin PW : admin

### 주요 기능

- Weka Library를 사용하여 과거 당첨 번호를 분석하고, 다양한 머신러닝 알고리즘을 통해 5개의 예측 번호를 도출합니다.
- 아이디당 일주일에 한 번만 예측 번호를 받아볼 수 있도록 하고, 스케줄링을 통해 발급된 내역을 일주일에 한번 삭제하도록 하였습니다.
- 다양한 클라이언트에서 접근할 수 있도록 RESTful API 설계·구현하였습니다.
- 사용자의 편의를 위해 소셜 로그인을 구현하여 쉽게 회원 가입 및 로그인이 가능하도록 하였습니다.
- Jenkins와 Docker, docker-compose를 통해 자동으로 빌드 및 배포가 진행되도록 설정하였습니다.

### 🚀 Backend Skills

---
![시스템 아키텍처 drawio](https://github.com/user-attachments/assets/fbb27a26-79ff-4812-a760-082a806fd9cb)
- Spring Boot 3.0.6
- Java 17
- MariaDB
- Spring Data Jpa
- Spring Security & OAuth2 & JWT
- Spring Rest Docs
- SMTP
- Weka
- Ngrok, WSL(Ubuntu)
- AWS Infra(EC2, Route 53)
- Nginx, CertBot
- Docker, docker-compose
- Jenkins

#### 기술적 의사 결정

>1. 로드밸런서, 타겟 그룹, ACM vs Nginx, CertBot
>- AWS 로드밸런서와 ACM을 우선적으로 고려했으나, 프로젝트의 규모와 비용 효율성을 감안했을 때 Nginx와 CertBot이 더 적합하다고 판단했습니다. Nginx는 리버스 프록시 서버로서 유연성이 좋고, 다양한 설정을 통해 안정성을 높일 수 있습니다. CertBot은 무료 SSL 인증서를 발급받고 자동 갱신할 수 있어 관리가 용이합니다.

>2. Docker, docker-compose, Jenkins를 사용한 이유
>- 애플리케이션의 환경 일관성을 보장하고, 이식성을 향상시키기 위해서입니다. Docker-compose는 복잡한 멀티 컨테이너 애플리케이션을 쉽게 정의하고 실행할 수 있게 해주며, Jenkins는 CI/CD 파이프라인을 통해 자동화된 빌드, 테스트, 배포를 가능하게 합니다.

>3. Jenkins를 로컬 환경에서 빌드한 이유
>- 로컬 환경에서 Jenkins를 빌드한 이유는 개발과 테스트를 빠르게 반복할 수 있도록 하기 위함입니다. 로컬 환경에서 CI/CD 파이프라인을 설정하면 코드 변경 사항을 즉시 확인할 수 있고, 외부 서버에 의존하지 않기 때문에 네트워크 지연이나 외부 요인에 영향을 받지 않습니다. 

>4. 프론트엔드 툴로 리액트를 선택한 이유
>- 리액트는 배우기 쉽고 대중적인 프론트엔드 라이브러리입니다. 컴포넌트 기반 아키텍처로 인해 재사용성과 유지보수성이 뛰어나며, 큰 커뮤니티와 풍부한 생태계를 가지고 있어 다양한 라이브러리와 도구를 쉽게 활용할 수 있습니다. 또한, 리액트는 빠른 렌더링 성능을 제공하여 사용자 경험을 향상시킬 수 있습니다.

>5. WSL로 로컬 환경을 리눅스 환경으로 만든 이유
>- Windows 환경보다 Linux 환경에서 Jenkins가 더 안정적이고 성능이 뛰어나기 때문입니다. Jenkins는 Linux 기반 서버에서 더 원활하게 동작하며, 리소스 관리와 스크립트 자동화에서 많은 이점을 제공합니다. WSL을 사용하면 로컬에서 Linux 환경을 손쉽게 설정할 수 있어, 개발 환경과 실제 배포 환경을 일치시켜 호환성 문제를 줄이고, 개발 효율성을 높일 수 있습니다.

### ✍ Achieved

---
#### 트러블슈팅 & 요구사항 해결

- [Ngrok을 이용한 Webhook 설정 및 로컬 서버 공개](https://velog.io/@studyjun/Ngrok%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-Webhook-%EC%84%A4%EC%A0%95-%EB%B0%8F-%EB%A1%9C%EC%BB%AC-%EC%84%9C%EB%B2%84-%EA%B3%B5%EA%B0%9C)
- [배포 환경에서 OAuth2 소셜 로그인을 위한 Nginx 설정 문제 해결](https://velog.io/@studyjun/%EB%B0%B0%ED%8F%AC-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-OAuth2-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%9D%84-%EC%9C%84%ED%95%9C-Nginx-%EC%84%A4%EC%A0%95-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)
- [Windows 환경에서 발생하는 Jenkins 에러를 Linux 환경으로 전환하여 해결](https://velog.io/@studyjun/Windows-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%EB%B0%9C%EC%83%9D%ED%95%98%EB%8A%94-Jenkins-%EC%97%90%EB%9F%AC%EB%A5%BC-Linux-%ED%99%98%EA%B2%BD%EC%9C%BC%EB%A1%9C-%EC%A0%84%ED%99%98%ED%95%98%EC%97%AC-%ED%95%B4%EA%B2%B0)
- [Weka 데이터 분석 모델의 패턴 인식 모델 오류 해결](https://velog.io/@studyjun/Weka-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%B6%84%EC%84%9D-%EB%AA%A8%EB%8D%B8%EC%9D%98-%ED%8C%A8%ED%84%B4-%EC%9D%B8%EC%8B%9D-%EB%AA%A8%EB%8D%B8-%EC%98%A4%EB%A5%98-%ED%95%B4%EA%B2%B0)
- [EC2 인스턴스 용량 문제 해결](https://velog.io/@studyjun/EC2-%EC%9D%B8%EC%8A%A4%ED%84%B4%EC%8A%A4-%EC%9A%A9%EB%9F%89-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0)

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
├─ 📜docker-compose.yml
└─ 📜Jenkinsfile
```

### Improvement Backlog

---

- [~~Jenkins 빌드 시 DB 메모리 초기화 문제~~](https://velog.io/@studyjun/Jenkins-%EB%B9%8C%EB%93%9C-%EC%8B%9C-DB-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%B4%88%EA%B8%B0%ED%99%94-%EB%AC%B8%EC%A0%9C)
- [~~Google 소셜 로그인 진행 시 닉네임 설정 문제~~](https://velog.io/@studyjun/Google-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%A7%84%ED%96%89-%EC%8B%9C-%EB%8B%89%EB%84%A4%EC%9E%84-%EC%84%A4%EC%A0%95-%EB%AC%B8%EC%A0%9C)
- [~~Jenkins 느린 빌드 시간 문제~~](https://velog.io/@studyjun/Jenkins-%EB%8A%90%EB%A6%B0-%EB%B9%8C%EB%93%9C-%EC%8B%9C%EA%B0%84-%EB%AC%B8%EC%A0%9C)
- [~~SSL 인증서 만료 시 자동 갱신 문제~~](https://velog.io/@studyjun/SSL-%EC%9D%B8%EC%A6%9D%EC%84%9C-%EB%A7%8C%EB%A3%8C-%EC%8B%9C-%EC%9E%90%EB%8F%99-%EA%B0%B1%EC%8B%A0-%EB%AC%B8%EC%A0%9C)
- [~~Prometheus + grafana + Alertmanager 모니터링, 시각화 및 알림~~](https://velog.io/@studyjun/Prometheus-Grafana-Alertmanager-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%8B%9C%EA%B0%81%ED%99%94-%EB%B0%8F-%EC%95%8C%EB%A6%BC)
- Jmeter를 통한 부하테스트
- 무중단 배포
- ngrok 고정 도메인
- 결제를 통한 포인트 획득 및 로또 번호 확인 시 사용