package xyz.tomorrowlearncamp.bookking.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.review.entity.Review;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateResponse {
    private Long reviewId;
    private String content;
    private Long userId;
    private StarRating rating;
    private Long bookId;

    public static ReviewCreateResponse toDto(Review review) {
        return new ReviewCreateResponse(
                review.getReviewId(),
                review.getContent(),
                review.getUser().getId(),
                review.getRating(),
                review.getBook().getBookId()
        );
    }
}
