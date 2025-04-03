package xyz.tomorrowlearncamp.bookking.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.ReviewState;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;
import xyz.tomorrowlearncamp.bookking.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StarRating rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewState reviewState;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    public void updateReview(String content, StarRating rating) {
        this.content = content;
        this.rating = rating;
    }

    public void deleteReview() {
        this.reviewState = ReviewState.INACTIVE;
    }
}