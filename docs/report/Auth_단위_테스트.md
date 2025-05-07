# Auth 서비스 단위 테스트

## 테스트 대상 상세

- AuthService 및 AuthController에 포함된 인증 로직

## 테스트 목적
- 회원가입 시 이메일 중복 여부 검증
- 로그인 시 비밀번호 매칭 및 JWT 토큰 생성 확인
- 유효한 Refresh Token을 기반으로 Access Token 재발급 검증
- 잘못된 인증 정보 또는 토큰 전달 시 적절한 예외 처리 검증

## 가정 및 필요 조건
- JWT 토큰 발급 및 검증은 mock 처리
- DB 저장/조회는 In-Memory 기반 (H2 or Mockito Stub)

## 테스트 시나리오 목록
| ID  | 테스트 이름                    | 전제조건                            | 테스트 단계 요약                                                                 | 입력값                                                                 | 기댓값                                                   | 성공 여부 |
|-----|-------------------------------|-------------------------------------|----------------------------------------------------------------------------------|------------------------------------------------------------------------|------------------------------------------------------------|-----------|
| 1   | 회원가입 성공                 | 중복 이메일 아님                     | 1. signup() 호출<br>2. 이메일 중복 체크<br>3. 비밀번호 인코딩 후 저장           | `authService.signup(SignupRequest.of("user@example.com", "pw", "name"))` | SignupResponse 반환<br>user 저장 성공                     | ✅         |
| 2   | 회원가입 실패 (중복 이메일)   | 이미 존재하는 이메일                | 1. signup() 호출<br>2. 이메일 중복 확인 → true                                   | `authService.signup(SignupRequest.of("duplicate@example.com", "pw", "name"))` | InvalidRequestException 발생                            | ✅         |
| 3   | 로그인 성공                   | 유저 존재, 패스워드 일치            | 1. signin() 호출<br>2. 이메일 조회<br>3. 비밀번호 일치<br>4. JWT 생성           | `authService.signin(LoginRequest.of("user@example.com", "pw"))`        | SignInResponse 반환                                      | ✅         |
| 4   | 로그인 실패 (없는 유저)       | 이메일로 유저가 없음               | 1. signin() 호출<br>2. 유저 조회 실패                                            | `authService.signin(LoginRequest.of("notfound@example.com", "pw"))`    | NotFoundException 발생                                  | ✅         |
| 5   | 로그인 실패 (비밀번호 불일치) | 유저 존재, 패스워드 불일치          | 1. signin() 호출<br>2. 패스워드 불일치                                           | `authService.signin(LoginRequest.of("user@example.com", "wrongpw"))`   | InvalidRequestException 발생                            | ✅         |
| 6   | 액세스 토큰 재발급 성공      | 유효한 RefreshToken 존재, 만료 아님 | 1. refreshAccessToken() 호출<br>2. RefreshToken 검증<br>3. 사용자 조회<br>4. 발급 | `authService.refreshAccessToken("valid-refresh-token")`               | AccessTokenResponse 반환<br>새로운 AccessToken 포함     | ✅         |
| 7   | 재발급 실패 (토큰 없음)      | 토큰이 DB에 없음                    | 1. refreshAccessToken() 호출<br>2. 조회 실패                                     | `authService.refreshAccessToken("invalid-token")`                     | InvalidRequestException 발생                            | ✅         |
| 8   | 재발급 실패 (토큰 만료)      | 토큰은 존재하나 만료됨              | 1. refreshAccessToken() 호출<br>2. 만료 시간 체크                                | `authService.refreshAccessToken("expired-token")`                     | InvalidRequestException 발생                            | ✅         |
| 9   | 이메일 중복 확인 성공         | 사용 가능한 이메일                  | 1. validateEmail() 호출<br>2. 이메일 미존재 확인                                 | `authService.validateEmail("available@example.com")`                  | 예외 없음                                               | ✅         |
| 10  | 이메일 중복 확인 실패         | 이미 존재하는 이메일                | 1. validateEmail() 호출<br>2. 이메일 존재 확인                                   | `authService.validateEmail("used@example.com")`                       | IllegalArgumentException 발생                           | ✅         |
