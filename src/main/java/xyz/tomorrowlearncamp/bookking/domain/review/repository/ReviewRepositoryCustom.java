package xyz.tomorrowlearncamp.bookking.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;

import java.util.Optional;

public interface ReviewRepositoryCustom {

    boolean existsByUserAndBookAndState(Long userId, Long bookId, ReviewState state);

    Page<Review> findByBookIdAndState(Long bookId, ReviewState state, Pageable pageable);

    Page<Review> findByUserIdAndState(Long userId, ReviewState state, Pageable pageable);

    Optional<Review> findByIdAndUserIdAndBookIdAndState(Long reviewId, Long userId, Long bookId, ReviewState state);
}
