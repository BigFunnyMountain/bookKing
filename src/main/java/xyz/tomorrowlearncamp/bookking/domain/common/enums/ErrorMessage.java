package xyz.tomorrowlearncamp.bookking.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

	REDIS_ERROR("SERVER ERROR"),
	;

	private final String errorMessage;
}
