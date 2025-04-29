package xyz.tomorrowlearncamp.bookking.domain.common.exception;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;

@Getter
public class ServerException extends RuntimeException {

	private final ErrorMessage errorMessage;

	public ServerException(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}
}
