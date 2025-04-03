package xyz.tomorrowlearncamp.bookking.domain.review.enums;

import lombok.Getter;

@Getter
public enum StarRating {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    StarRating(int value) {
        this.value = value;
    }
}
