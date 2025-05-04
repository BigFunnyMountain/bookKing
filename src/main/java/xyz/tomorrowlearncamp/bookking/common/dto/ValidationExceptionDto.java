package xyz.tomorrowlearncamp.bookking.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationExceptionDto {

	private final String code;
	private final String field;
	private final String message;
}
