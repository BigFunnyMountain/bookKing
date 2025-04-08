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

    @Query("SELECT COUNT(r) > 0 " + "FROM Review r " + "WHERE r.userId = :userId " + "AND r.bookId = :bookId " + "AND r.reviewState = :state")
    boolean existsByUserAndBookAndState(@Param("userId") Long userId,
                                        @Param("bookId") Long bookId,
                                        @Param("state") ReviewState state);

    @Query("SELECT r " + "FROM Review r " + "WHERE r.bookId = :bookId " + "AND r.reviewState = :reviewState " + "ORDER BY r.createdAt DESC")
    Page<Review> findByBookIdAndState(@Param("bookId") Long bookId,
                                      @Param("reviewState") ReviewState reviewState,
                                      Pageable pageable);

    @Query("SELECT r " + "FROM Review r " + "WHERE r.userId = :userId " + "AND r.reviewState = :reviewState " + "ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdAndState(@Param("userId") Long userId,
                                      @Param("reviewState") ReviewState reviewState,
                                      Pageable pageable);

    @Query("SELECT r " + "FROM Review r " + "WHERE r.reviewId = :reviewId " + "AND r.userId = :userId " + "AND r.bookId = :bookId " + "AND r.reviewState = :reviewState")
    Optional<Review> findByIdAndUserIdAndBookIdAndState(@Param("reviewId") Long reviewId,
                                                        @Param("userId") Long userId,
                                                        @Param("bookId") Long bookId,
                                                        @Param("reviewState") ReviewState reviewState);
}
