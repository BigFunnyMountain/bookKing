# Book 서비스 단위 테스트
							
## 테스트 대상 상세				
- BookService의 도서 등록, 수정, 삭제, 재고 관리 기능							
							
## 테스트 시나리오 목적 
- 도서 정보의 CRUD 기능이 정상적으로 동작하는지 검증하고, 재고 관리 로직의 정확성을 확인							
							
## 가정 및 필요사항 					
- 데이터베이스에 사전 등록된 도서 정보가 존재
- 관리자 권한을 가진 사용자로 테스트 수행
- 외부 시스템 연동 없이 단위 테스트 환경에서 실행

## 테스트 시나리오 목록
| ID  | 테스트 이름               | 전제조건                                                         | 테스트 단계 요약                                                                           | 입력값                                                                                     | 기댓값                                                   | 성공 여부 |
|-----|----------------------------|--------------------------------------------------------------------|--------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|------------------------------------------------------------|-----------|
| 1   | 책 등록 성공               | 1. 로그인된 사용자<br>2. `ROLE_ADMIN` 권한<br>3. 유효한 요청 DTO  | `addBook()` 호출 → 권한 체크 → 서비스 호출 → Book 저장 후 ID 반환                         | `bookController.addBook(userRole=ROLE_ADMIN, requestDto)`                                | 책 등록 성공, HTTP 200 OK, 등록된 Book의 ID 반환           | ✅         |
| 2   | 책 등록 권한 없음          | 1. 로그인된 사용자<br>2. `ROLE_USER` 권한                         | `addBook()` 호출 → 권한 체크 실패 → 예외 발생                                              | `bookController.addBook(userRole=ROLE_USER, requestDto)`                                 | `ForbiddenRequestException` 발생                           | ✅         |
| 3   | 책 등록 시 입력값 누락     | 1. `ROLE_ADMIN` 로그인<br>2. DTO 일부 필드 누락                   | `addBook()` 호출 → DTO Validation 실패                                                    | `bookController.addBook(userRole=ROLE_ADMIN, requestDtoWithoutTitle)`                    | `MethodArgumentNotValidException` 발생                     | ✅         |
| 4   | 책 정보 수정 성공          | 1. `ROLE_ADMIN` 사용자<br>2. 유효한 bookId 존재                   | `updateBook()` 호출 → 권한 및 존재 여부 확인 → 수정 수행                                  | `bookController.updateBook(userRole=ROLE_ADMIN, bookId=1L, updateDto)`                   | 책 정보 수정됨                                            | ✅         |
| 5   | 책 삭제 성공               | 1. `ROLE_ADMIN` 사용자<br>2. 삭제 대상 Book 존재                  | `deleteBook()` 호출 → 권한 확인 → Book 삭제 수행                                          | `bookController.deleteBook(userRole=ROLE_ADMIN, bookId=1L)`                               | 책 삭제 완료                                              | ✅         |
| 6   | 책 삭제 시 권한 없음       | 1. `ROLE_USER` 사용자                                             | `deleteBook()` 호출 → 권한 체크 실패                                                       | `bookController.deleteBook(userRole=ROLE_USER, bookId=1L)`                                | `ForbiddenRequestException` 발생                           | ✅         |
| 7   | 책 재고 수정 성공          | 1. `ROLE_ADMIN` 사용자<br>2. 해당 책 존재                         | `updateStock()` 호출 → 권한 확인 → 재고 수정 반영                                          | `bookController.updateBookStock(userRole=ROLE_ADMIN, bookId=1L, stock=100)`              | 책 재고 수정 성공                                         | ✅         |
| 8   | 책 검색 성공               | 1. Elasticsearch에 책 데이터 존재                                 | `getRelatedBooks()` 호출 → 검색어 전달 → 연관 검색어 결과 반환                            | `bookController.getRelatedBooks(keyword="자바")`                                          | 연관 키워드 포함한 책 목록 반환                           | ✅         |
| 9   | 검색어 누락                | 1. `keyword` 파라미터 누락                                        | `getRelatedBooks()` 호출 → `keyword=null`                                                 | `bookController.getRelatedBooks(keyword=null)`                                            | HTTP 400 Bad Request 발생                                 | ✅         |
