package xyz.tomorrowlearncamp.bookking.domain.common.dto;

import lombok.Getter;
import lombok.ToString;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;

@Getter
@ToString()
public class CustomExceptionDto {

	private final String codeName;
	private final String message;

	public CustomExceptionDto(ErrorMessage errorMessage) {
		this.codeName = errorMessage.name();
		this.message = errorMessage.getMessage();
	}
}
