
### 개요

---

ECS Health Check와 ALB 대상 그룹의 Health Check가 `OUT_OF_SERVICE` 로 되어 있어서 모두 실패하는 문제가 발생했다.  

해당 문제를 해결하기 위해 spring-actuator 기능을 활용해서 스프링 애플리케이션과 연결된 서비스들에 문제가 있는지 확인 후 해결했다.

spring-actuator에서 `{ “STATUS”: “DOWN” }` 은 많이 봤지만 `{ “STATUS”: “OUT_OF_SERVICE” }` 는 처음 봤기 때문에 해결 과정에 대해서 자세히 다뤄보려고 한다.

### 문제상황

---

문제 발생 이틀 전 애플리케이션이 문제 없이 실행되는 것을 확인하고, 문제 발생 당일 성능 테스트를 진행하기 위해 EC2 인스턴스와 ECS Task를 실행시켰었다. 

task가 정상적으로 실행되는 것을 확인했지만 Health Check 결과 Unhealthy가 나오고 있었다. 

애플리케이션과 연결된 서비스들을 도커 명령어를 통해 실행시켜 주었고, cloudwatch logs에서 애플리케이션 기동 로그를 살펴봐도 연결과 관련된 문제는 확인할 수 없었다.

### 원인 분석

---

<aside>
<img src="/icons/checkmark-line_green.svg" alt="/icons/checkmark-line_green.svg" width="40px" />

**보안 그룹 확인**

---

인프라의 문제는 대부분 보안 그룹에서 발생하긴 하지만 정상적으로 동작했을 때와 달라진 것이 없었고, health check API 호출 시 연결 실패가 발생한 것이 아니라 API 요청하고 응답 받은 것엔 문제가 없었기 때문에 보안그룹은 문제의 원인이 아니라고 판단했다.

</aside>

<aside>
<img src="/icons/checkmark-line_green.svg" alt="/icons/checkmark-line_green.svg" width="40px" />

</aside>

<aside>
<img src="/icons/checkmark-line_green.svg" alt="/icons/checkmark-line_green.svg" width="40px" />

**대상 그룹의 health check 프로토콜 확인**

---

대상 그룹에서 기존 health check API에 HTTP로 보내던 프로토콜을 HTTPS로 바꿔서 보내봤지만 바꾼 이후에 오히려 다른 에러 로그가 올라오기 시작했기 때문에 대상 그룹의 프로토콜은 문제의 원인이 아니라고 판단했다.

</aside>

<aside>
<img src="/icons/checkmark-line_green.svg" alt="/icons/checkmark-line_green.svg" width="40px" />

**ECS Task에 정의된 health check 경로 변경**

---

기존 ECS Task에 [`http://localhost:8080/actuator/health`](http://localhost:8080/actuator/health) 로 health check를 보냈었지만 `localhost` 와 `127.0.0.1` 을 다르게 받을 수도 있다고 생각해서 바꿔서 보내봤지만 문제가 해결되지 않았기 때문에 health check 경로는 문제의 원인이 아니라고 판단했다.

</aside>

<aside>
<img src="/icons/checkmark-line_green.svg" alt="/icons/checkmark-line_green.svg" width="40px" />

**최신 Docker 이미지로 변경해서 확인**

---

근본적인 해결 방법은 아니지만 최신 도커 이미지를 적용시켰을 때 해결이 될 수도 있을 것이라 생각해서 적용시켜 봤지만 문제가 해결되지 않았기 때문에 애플리케이션 버전에 대한 문제도 아니라고 판단했다.

</aside>

<aside>
<img src="/icons/close_red.svg" alt="/icons/close_red.svg" width="40px" />

spring-actuator의 모든 내용 확인

---

로컬에서 `application.yml` 에 정의된 spring-actuator의 표시 수준을 
`when_authorized` **에서 `always` 로 변경 후 `/actuator/health` 에서 확인했다. 그 결과 애플리케이션과 연결된 elasticsearch의 상태가 `OUT_OF_SERVICE` 로 되어 있었고, elasticsearch가 문제라고 판단하게 되었다.

</aside>

### 해결방법

---

먼저 elasticsearch가 있는 EC2에 접속해서 `docker logs ela` 를 통해 로그를 확인했다. 로그에서 예외가 발생한 흔적을 찾을 수 있었고, 확인 결과 특정 텍스트 파일이 없어서 예외가 발생하고 있었다. 

정확한 확인을 위해 `docker exec -it ela bash` 명령어로 elasticsearch 컨테이너로 들어가서 확인한 결과 EC2 인스턴스에는 있던 텍스트 파일이 도커 컨테이너에는 없었다.

### 적용

---

먼저 `docker-compose.yml` 파일을 확인했고, elasticsearch 서비스를 정의하는 과정에서 `volume`이 잘못 설정되어 있는 것을 확인했다. 따라서 `volume`을 올바르게 정의하고, `docker-compose down` → `docker-compose up -d` 명령어를 통해 적용시켰다.

이후 elasticsearch 컨테이너로 들어가서 텍스트 파일을 `cp` 명령어를 통해 `config` 로 복사해준 뒤 ECS Task를 실행시켰다.

### 결과

---

`/actuator/health` 로 접근했을 때 모든 서비스가 `UP` 으로 표시된 것을 확인했고, spring-actuator의 표시 수준을 다시 `when_authorized` **로 변경했다.
