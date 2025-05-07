# 서비스 단위 테스트 1차

## 테스트 대상 상세
- UserService 및 UserController의 사용자 관련 기능 전반
- 회원 정보 조회, 수정, 삭제, 권한 변경, 프로필 이미지 업로드 등

## 테스트 목적
- 사용자 정보 조회 시 유효한 사용자만 접근 가능한지 검증
- 사용자 정보 수정이 정상적으로 반영되는지 확인
- 비밀번호 일치 여부에 따라 탈퇴 처리가 올바르게 수행되는지 검증
- 역할(Role) 변경 시 동일한 역할로는 변경되지 않도록 예외 처리 확인
- 이미지 업로드 시 외부 S3 서비스와의 연동 및 사용자 상태 반영 확인

## 가정 및 필요 조건
- 인증된 사용자 정보는 @AuthenticationPrincipal로 주입된다고 가정
- 삭제된 사용자(deletedAt != null)는 모든 기능에 접근 불가
- 이미지 업로드는 실제 S3가 아닌 Mock 객체로 테스트
- 사용자 역할은 UserRole enum으로 관리되며 변경 전후 상태 비교 필요
- 비밀번호 비교는 PasswordEncoder 모킹 또는 실제 인코딩 문자열 사용

## 테스트 시나리오 목록
| ID  | 테스트 이름                    | 전제조건                                                             | 테스트 단계 요약                                                                                      | 입력값                                                                                              | 기댓값                              | 성공 여부 |
|-----|-------------------------------|----------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|--------------------------------------|-----------|
| 1   | 내 정보 조회 성공             | 1. 로그인한 사용자<br>2. 유저가 존재하며 삭제되지 않음                | 1. `getMyInfo()` 호출<br>2. `userRepository.findById()` 수행                                           | `userService.getMyInfo(1L)`                                                                          | `UserMyInfoResponse` 반환           | ✅         |
| 2   | 내 정보 조회 실패             | 로그인했지만 유저가 없거나 soft delete 상태                         | 1. `getMyInfo()` 호출<br>2. `findById()` → Optional.empty                                              | `userService.getMyInfo(999L)`                                                                        | `NotFoundException` 발생            | ✅         |
| 3   | 사용자 정보 수정 성공         | 1. 로그인된 유저<br>2. 삭제되지 않음<br>3. 유효한 수정값 전달         | 1. `updateUser()` 호출<br>2. `findById()` 조회<br>3. 수정 반영 및 저장                                | `userService.updateUser(1L, new UpdateUserRequest("닉네임", "소개"))`                                | 사용자 정보 수정됨                  | ✅         |
| 4   | 사용자 삭제 성공              | 1. 유저 존재<br>2. 비밀번호 일치                                     | 1. `deleteUser()` 호출<br>2. `findById()` 조회<br>3. 패스워드 비교<br>4. `deletedAt` 설정              | `userService.deleteUser(1L, new PasswordRequest("비밀번호"))`                                        | soft delete 처리됨                  | ✅         |
| 5   | 사용자 삭제 실패 (비번 불일치) | 1. 유저 존재<br>2. 비밀번호 불일치                                   | 1. `deleteUser()` 호출<br>2. 비밀번호 비교 실패                                                        | `userService.deleteUser(1L, new PasswordRequest("틀린비번"))`                                        | `InvalidRequestException` 발생      | ✅         |
| 6   | 역할 변경 성공                | 1. 유저 존재<br>2. 현재 역할과 다른 역할 요청                         | 1. `updateUserRole()` 호출<br>2. 현재 역할 확인<br>3. 변경 수행                                       | `userService.updateUserRole(1L, ROLE_ADMIN)`                                                         | 역할 변경 성공                      | ✅         |
| 7   | 역할 변경 실패 (동일 역할)    | 현재와 동일한 역할로 변경 요청                                       | 1. `updateUserRole()` 호출<br>2. 현재 역할과 동일                                                      | `userService.updateUserRole(1L, ROLE_USER)`                                                          | `InvalidRequestException` 발생      | ✅         |
| 8   | 이미지 업로드 성공            | 1. 유저 존재<br>2. S3Service는 Mock으로 구성되어 있음                 | 1. `uploadUserImage()` 호출<br>2. S3 URL 반환<br>3. User 엔티티에 저장                                | `userService.uploadUserImage(1L, MultipartFile)`                                                     | 프로필 이미지 저장                  | ✅         |
