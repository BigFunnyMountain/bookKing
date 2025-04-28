package xyz.tomorrowlearncamp.bookking.domain.common.exception;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;

@Getter
public class InvalidRequestException extends RuntimeException {

	private final ErrorMessage errorMessage;

	public InvalidRequestException(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}
}
