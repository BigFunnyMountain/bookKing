package xyz.tomorrowlearncamp.bookking.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StarRating rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewState reviewState;

    @Builder
    public Review(Long userId, Long bookId, StarRating rating, String content, ReviewState reviewState) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.content = content;
        this.reviewState = reviewState;
    }


    public void updateReview(String content, StarRating rating) {
        this.content = content;
        this.rating = rating;
    }

    public void deleteReview() {
        this.reviewState = ReviewState.INACTIVE;
    }
}