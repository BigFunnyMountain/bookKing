package xyz.tomorrowlearncamp.bookking.domain.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;
import xyz.tomorrowlearncamp.bookking.domain.review.repository.ReviewRepository;
import xyz.tomorrowlearncamp.bookking.domain.review.service.ReviewService;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage.PURCHASE_HISTORY_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void 리뷰_작성_성공() {
        // given
        Long userId = 1L;
        Long bookId = 2L;
        Long orderId = 3L;

        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = Book.builder().bookId(bookId).build();

        ReviewRequest request = new ReviewRequest();
        ReflectionTestUtils.setField(request, "rating", StarRating.FIVE);
        ReflectionTestUtils.setField(request, "content", "좋은 책이에요!");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(reviewRepository.existsByUserAndBookAndState(userId, bookId, ReviewState.ACTIVE)).willReturn(false);
        given(orderService.getPurchasedOrderId(userId, bookId)).willReturn(orderId);

        Review savedReview = Review.builder()
                .userId(userId)
                .bookId(bookId)
                .rating(StarRating.FIVE)
                .content("좋은 책이에요!")
                .reviewState(ReviewState.ACTIVE)
                .build();

        ReflectionTestUtils.setField(savedReview, "reviewId", 10L);


        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

        // when
        ReviewResponse response = reviewService.saveReview(userId, bookId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReviewId()).isEqualTo(10L);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getBookId()).isEqualTo(bookId);
        assertThat(response.getContent()).isEqualTo("좋은 책이에요!");
        assertThat(response.getRating()).isEqualTo(StarRating.FIVE);

        verify(orderService).switchReviewStatus(orderId);
    }

    @Test
    void 리뷰_작성_실패_구매내역없음() {
        // given
        Long userId = 1L;
        Long bookId = 2L;

        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = Book.builder().bookId(bookId).build();

        ReviewRequest request = new ReviewRequest();
        ReflectionTestUtils.setField(request, "rating", StarRating.FIVE);
        ReflectionTestUtils.setField(request, "content", "내용");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(reviewRepository.existsByUserAndBookAndState(userId, bookId, ReviewState.ACTIVE)).willReturn(false);
        given(orderService.getPurchasedOrderId(userId, bookId))
                .willThrow(new NotFoundException(PURCHASE_HISTORY_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> reviewService.saveReview(userId, bookId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("구매 이력이 존재하지 않습니다.");
    }

    @Test
    void 리뷰_작성_실패_이미작성된_리뷰() {
        // given
        Long userId = 1L;
        Long bookId = 2L;

        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = Book.builder().bookId(bookId).build();

        ReviewRequest request = new ReviewRequest();
        ReflectionTestUtils.setField(request, "rating", StarRating.FIVE);
        ReflectionTestUtils.setField(request, "content", "내용");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(reviewRepository.existsByUserAndBookAndState(userId, bookId, ReviewState.ACTIVE)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.saveReview(userId, bookId, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이미 리뷰를 작성한 사용자입니다.");
    }

    @Test
    void 리뷰_삭제_성공() {
        // given
        Long userId = 1L;
        Long bookId = 2L;
        Long reviewId = 3L;
        Long orderId = 4L;

        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = Book.builder().bookId(bookId).build();

        Review savedReview = Review.builder()
                .userId(userId)
                .bookId(bookId)
                .rating(StarRating.FIVE)
                .content("좋은 책이에요!")
                .reviewState(ReviewState.ACTIVE)
                .build();

        ReflectionTestUtils.setField(savedReview, "reviewId", 10L);


        given(reviewRepository.findByIdAndUserIdAndBookIdAndState(reviewId, userId, bookId, ReviewState.ACTIVE))
                .willReturn(Optional.of(savedReview));
        given(orderService.getPurchasedOrderId(userId, bookId)).willReturn(orderId);

        // when
        reviewService.deleteReview(userId, bookId, reviewId);

        // then
        assertThat(savedReview.getReviewState()).isEqualTo(ReviewState.INACTIVE);
        verify(orderService).switchReviewStatus(orderId);
    }

    @Test
    void 리뷰_삭제_실패_리뷰없음() {
        // given
        Long userId = 1L;
        Long bookId = 2L;
        Long reviewId = 3L;

        given(reviewRepository.findByIdAndUserIdAndBookIdAndState(reviewId, userId, bookId, ReviewState.ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(userId, bookId, reviewId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("리뷰가 존재하지 않거나 권한이 없습니다.");
    }
}
