package xyz.tomorrowlearncamp.bookking.common.exception;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;

@Getter
public class ForbiddenRequestException extends RuntimeException {

    private final ErrorMessage errorMessage;
    public ForbiddenRequestException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}