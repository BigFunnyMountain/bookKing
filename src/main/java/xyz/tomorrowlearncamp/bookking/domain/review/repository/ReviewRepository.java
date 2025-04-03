package xyz.tomorrowlearncamp.bookking.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.book.bookId = :bookId AND r.reviewState = :reviewState")
    boolean existsByUserAndBookAndState(@Param("userId") Long userId,
                                        @Param("bookId") Long bookId,
                                        @Param("reviewState") ReviewState reviewState);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.book.bookId = :bookId AND r.reviewState = :reviewState")
    Page<Review> findByBookIdAndState(@Param("bookId") Long bookId,
                                      @Param("reviewState") ReviewState reviewState,
                                      Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.user.id = :userId AND r.reviewState = :reviewState")
    Page<Review> findByUserIdAndState(@Param("userId") Long userId,
                                      @Param("reviewState") ReviewState reviewState,
                                      Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.user.id = :userId AND r.book.bookId = :bookId AND r.reviewState = :reviewState")
    Optional<Review> findByIdAndUserIdAndBookIdAndState(@Param("reviewId") Long reviewId,
                                                        @Param("userId") Long userId,
                                                        @Param("bookId") Long bookId,
                                                        @Param("reviewState") ReviewState reviewState);
}
