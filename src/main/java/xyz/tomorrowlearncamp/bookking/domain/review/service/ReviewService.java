package xyz.tomorrowlearncamp.bookking.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewUpdateRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewCreateResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;
import xyz.tomorrowlearncamp.bookking.domain.review.repository.ReviewRepository;
import xyz.tomorrowlearncamp.bookking.user.entity.User;
import xyz.tomorrowlearncamp.bookking.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final OrderService orderService;

    @Transactional
    public ReviewCreateResponse saveReview(Long userId, Long bookId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        Book book = bookService.getBookById(bookId);

        boolean exists = reviewRepository.existsByUserAndBookAndState(userId, bookId, ReviewState.ACTIVE);
        if (exists) {
            throw new InvalidRequestException("이미 리뷰를 작성한 사용자입니다.");
        }

        if (!orderService.hasUserPurchasedBook(userId, bookId)) {
            throw new InvalidRequestException("리뷰는 해당 책을 구매한 사용자만 작성할 수 있습니다.");
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .content(request.getContent())
                .reviewState(ReviewState.ACTIVE)
                .build();

        return ReviewCreateResponse.toDto(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getBookReviews(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByBookIdAndState(bookId, ReviewState.ACTIVE, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByUserIdAndState(userId, ReviewState.ACTIVE, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public void updateReview(Long userId, Long bookId, Long reviewId, ReviewUpdateRequest request) {
        Review review = getReviewOwnedByUser(reviewId, userId, bookId);
        review.updateReview(request.getContent(), request.getRating());
    }

    @Transactional
    public void deleteReview(Long userId, Long bookId, Long reviewId) {
        Review review = getReviewOwnedByUser(reviewId, userId, bookId);
        review.deleteReview();
    }

    private Review getReviewOwnedByUser(Long reviewId, Long userId, Long bookId) {
        return reviewRepository.findByIdAndUserIdAndBookIdAndState(reviewId, userId, bookId, ReviewState.ACTIVE)
                .orElseThrow(() -> new NotFoundException("리뷰가 존재하지 않거나 권한이 없습니다."));
    }
}