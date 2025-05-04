package xyz.tomorrowlearncamp.bookking.common.exception;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;

@Getter
public class NotFoundException extends RuntimeException {

	private final ErrorMessage errorMessage;

	public NotFoundException(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}
}
