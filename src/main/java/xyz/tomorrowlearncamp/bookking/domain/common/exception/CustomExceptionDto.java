package xyz.tomorrowlearncamp.bookking.domain.common.exception;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;

@Getter
public class CustomExceptionDto {

	private final String codeName;
	private final String message;

	public CustomExceptionDto(ErrorMessage errorMessage) {
		this.codeName = errorMessage.name();
		this.message = errorMessage.getMessage();
	}
}
