### Auth API
| 기능 | method | URL | requestHeader | requestBody | responseBody | responseStatus |
|:----:|:------:|:---:|:-------------:|:-----------:|:------------:|:---------------:|
| <nobr>회원 가입</nobr> | POST | /api/v1/auth/signup | - | SignupRequest | SignupResponse | 200 OK<br>400 BAD_REQUEST |
| <nobr>유저 로그인 (email)</nobr> | POST | /api/v1/auth/login | - | LoginRequest | LoginResponse | 200 OK<br>400 BAD_REQUEST |
| <nobr>토큰 재발급</nobr> | POST | /api/v1/auth/refresh | Authorization | - | 재발급 Response | 200 OK<br>400 BAD_REQUEST |
