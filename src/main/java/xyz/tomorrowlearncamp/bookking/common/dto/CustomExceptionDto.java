package xyz.tomorrowlearncamp.bookking.common.dto;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;

@Getter
public class CustomExceptionDto {

	private final String codeName;
	private final String message;

	public CustomExceptionDto(ErrorMessage errorMessage) {
		this.codeName = errorMessage.name();
		this.message = errorMessage.getMessage();
	}
}
