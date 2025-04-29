package xyz.tomorrowlearncamp.bookking.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationExceptionDto {

	private final String code;
	private final String field;
	private final String message;
}
