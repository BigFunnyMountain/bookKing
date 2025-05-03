package xyz.tomorrowlearncamp.bookking.domain.review.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long userId;
    private StarRating rating;
    private String content;
    private LocalDateTime createdAt;
    private Long bookId;

    public static ReviewResponse of(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.reviewId = review.getId();
        response.userId = review.getUserId();
        response.rating = review.getRating();
        response.content = review.getContent();
        response.createdAt = review.getCreatedAt();
        response.bookId = review.getBookId();
        return response;
    }
}
