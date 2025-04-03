package xyz.tomorrowlearncamp.bookking.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.review.enums.StarRating;

@Getter
public class ReviewUpdateRequest {

    @NotNull(message = "별점은 필수입니다.")
    private StarRating rating;

    @NotBlank(message = "리뷰 내용은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "리뷰 내용은 1000자 이내로 작성해주세요.")
    private String content;
}
