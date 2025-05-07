# Review 서비스 단위 테스트

##  테스트 대상 상세
- ReviewService의 리뷰 작성, 수정, 삭제, 조회 기능

## 테스트 목적
- 리뷰 관련 기능이 정상적으로 동작하는지 확인하고,<br>
리뷰 상태 관리 로직의 정확성을 검증

## 가정 및 필요 조건
- 사용자가 리뷰를 작성할 수 있는 도서가 존재함
- 리뷰 상태 변경에 따른 비즈니스 로직이 명확히 정의되어 있음

## 테스트 시나리오 목록

| ID  | 테스트 이름           | 전제조건                                      | 테스트 단계 요약                                                     | 입력값                                                                                         | 기댓값                               | 성공 여부 |
|-----|------------------------|------------------------------------------------|----------------------------------------------------------------------|------------------------------------------------------------------------------------------------|----------------------------------------|------------|
| 1   | 책 리뷰 저장 성공      | 회원가입된 사용자, 구매 이력 있음, 리뷰 미작성 | saveReview() 호출 → 유저, 책, 주문, 리뷰 존재 여부 확인 후 저장     | `reviewService.saveReview(1L, 1L, new ReviewRequest(5, "좋은 책입니다"))`                   | 리뷰 저장 성공                          | ✅         |
| 2   | 리뷰 중복 방지         | 사용자 존재, 리뷰 이미 존재                    | saveReview() 호출 → existsActiveReview() = true → 예외 발생         | `reviewService.saveReview(1L, 1L, new ReviewRequest(5, "또 써요"))`                         | `InvalidRequestException` 발생         | ✅         |
| 3   | 책 없음               | 존재하지 않는 bookId 사용                      | saveReview() 호출 → findById() → 예외 발생                          | `reviewService.saveReview(1L, 999L, new ReviewRequest(4, "책 없음 테스트"))`               | `NotFoundException` 발생               | ✅         |
| 4   | 구매 이력 없음         | 책 미구매 상태                                 | saveReview() 호출 → 주문 이력 없음 확인 → 예외 발생                 | `reviewService.saveReview(1L, 3L, new ReviewRequest(5, "미구매 테스트"))`                   | `NotFoundException` 발생               | ✅         |
| 5   | 리뷰 수정 성공         | 유저가 작성한 리뷰 존재                        | updateReview() 호출 → 리뷰 조회 → 수정 반영                        | `reviewService.updateReview(1L, 1L, 10L, new ReviewUpdateRequest("내용수정", 4))`           | 리뷰 내용 및 평점 수정됨               | ✅         |
| 6   | 리뷰가 없는 경우       | 리뷰 작성 이력 없음                            | updateReview() 호출 → 리뷰 조회 실패 → 예외 발생                    | `reviewService.updateReview(1L, 1L, 999L, new ReviewUpdateRequest("없음", 3))`              | `NotFoundException` 발생               | ✅         |
| 7   | 리뷰 삭제 성공         | 리뷰 존재, 구매 이력 있음                      | deleteReview() 호출 → 상태 비활성 처리 + 주문 리뷰 상태 변경        | `reviewService.deleteReview(1L, 1L, 10L)`                                                    | 리뷰 비활성화 및 주문 상태 변경됨     | ✅         |
| 8   | 리뷰 삭제 대상 없음    | 리뷰 미작성 상태                               | deleteReview() 호출 → 리뷰 조회 실패 → 예외 발생                    | `reviewService.deleteReview(1L, 1L, 999L)`                                                   | `NotFoundException` 발생               | ✅         |
| 9   | 내 리뷰 조회 성공      | ACTIVE 상태 리뷰 2개 존재                       | getMyReviews() 호출 → pagination 적용                              | `reviewService.getMyReviews(1L, 0, 10)`                                                     | `Page<ReviewResponse>` 반환           | ✅         |
| 10  | 리뷰 없음             | 작성된 리뷰 없음                               | getMyReviews() 호출 → 빈 결과                                       | `reviewService.getMyReviews(2L, 0, 10)`                                                     | 빈 페이지 반환                          | ✅         |
| 11  | 책 리뷰 조회 성공      | 특정 bookId에 대해 ACTIVE 상태 리뷰 존재        | getBookReviews() 호출                                               | `reviewService.getBookReviews(1L, 0, 10)`                                                   | `Page<ReviewResponse>` 반환           | ✅         |
| 12  | 책 리뷰 없음           | 해당 책에 리뷰 없음                            | getBookReviews() 호출 → 빈 결과                                     | `reviewService.getBookReviews(999L, 0, 10)`                                                 | 빈 페이지 반환                          | ✅         |
