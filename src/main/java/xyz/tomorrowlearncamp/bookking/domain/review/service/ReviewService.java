package xyz.tomorrowlearncamp.bookking.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.request.ReviewUpdateRequest;
import xyz.tomorrowlearncamp.bookking.domain.review.dto.response.ReviewResponse;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;
import xyz.tomorrowlearncamp.bookking.domain.review.repository.ReviewRepository;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderService orderService;

    @Transactional
    public ReviewResponse saveReview(Long userId, Long bookId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND));

        boolean exists = reviewRepository.existsByUserAndBookAndState(userId, bookId, ReviewState.ACTIVE);
        if (exists) {
            throw new InvalidRequestException(ErrorMessage.REVIEW_ALREADY_WRITTEN);
        }

        Long orderId = orderService.getPurchasedOrderId(userId, bookId);

        orderService.switchReviewStatus(orderId);

        Review review = Review.builder()
                .userId(userId)
                .bookId(bookId)
                .rating(request.getRating())
                .content(request.getContent())
                .reviewState(ReviewState.ACTIVE)
                .build();

        return ReviewResponse.of(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getBookReviews(Long bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByBookIdAndState(bookId, ReviewState.ACTIVE, pageable)
                .map(ReviewResponse::of);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByUserIdAndState(userId, ReviewState.ACTIVE, pageable)
                .map(ReviewResponse::of);
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

        Long orderId = orderService.getPurchasedOrderId(userId, bookId);

        orderService.switchReviewStatus(orderId);
    }

    private Review getReviewOwnedByUser(Long reviewId, Long userId, Long bookId) {
        return reviewRepository.findByIdAndUserIdAndBookIdAndState(reviewId, userId, bookId, ReviewState.ACTIVE)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.REVIEW_ALREADY_WRITTEN));
    }
}