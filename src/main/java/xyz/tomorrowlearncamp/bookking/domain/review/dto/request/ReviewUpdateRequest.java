package xyz.tomorrowlearncamp.bookking.domain.review.dto.request;

import static xyz.tomorrowlearncamp.bookking.domain.review.consts.ReviewConstants.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;

@Getter
public class ReviewUpdateRequest {

    @NotNull(message = RATING_REQUIRED_MESSAGE)
    private StarRating rating;

    @NotBlank(message = CONTENT_NOT_BLANK_MESSAGE)
    @Size(max = REVIEW_CONTENT_MAX_LENGTH, message = CONTENT_SIZE_MESSAGE)
    private String content;
}
