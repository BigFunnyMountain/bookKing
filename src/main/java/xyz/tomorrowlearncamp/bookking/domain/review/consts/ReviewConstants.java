package xyz.tomorrowlearncamp.bookking.domain.review.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ReviewConstants {

    public static final int REVIEW_CONTENT_MAX_LENGTH = 1000;

    public static final String RATING_REQUIRED_MESSAGE = "별점은 필수입니다.";
    public static final String CONTENT_NOT_BLANK_MESSAGE = "리뷰 내용은 비어 있을 수 없습니다.";
    public static final String CONTENT_SIZE_MESSAGE = "리뷰 내용은 1000자 이내로 작성해주세요.";
}
