package xyz.tomorrowlearncamp.bookking.domain.review.consts;

public interface ReviewConstants {

    int REVIEW_CONTENT_MAX_LENGTH = 1000;

    String RATING_REQUIRED_MESSAGE = "별점은 필수입니다.";
    String CONTENT_NOT_BLANK_MESSAGE = "리뷰 내용은 비어 있을 수 없습니다.";
    String CONTENT_SIZE_MESSAGE = "리뷰 내용은 1000자 이내로 작성해주세요.";
}
