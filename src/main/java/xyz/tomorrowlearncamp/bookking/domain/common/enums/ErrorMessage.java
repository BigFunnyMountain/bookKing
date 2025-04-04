package xyz.tomorrowlearncamp.bookking.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
	OPENAPI_ERROR("OPENAPI ERROR"),
	REDIS_ERROR("CACHE ERROR"),
	ERROR("알 수 없는 에러가 발생했습니다."),
	NOT_FOUND_BOOK("없는 책입니다."),
	;

	private final String message;
}
