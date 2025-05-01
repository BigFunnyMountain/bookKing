package xyz.tomorrowlearncamp.bookking.domain.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

	/* 4xx */
	ZERO_BOOK_STOCK(BAD_REQUEST,"남은 책이 없습니다."),
	SHORT_ON_MONEY(BAD_REQUEST,"돈이 부족합니다."),
	REVIEW_ALREADY_WRITTEN(BAD_REQUEST, "이미 작성된 리뷰입니다."),
	TYPE_MISMATCH(BAD_REQUEST, "올바른 값을 입력해주세요."),

	NO_AUTHORITY_TO_WRITE_A_REVIEW(FORBIDDEN, "리뷰를 작성할 권한이 없습니다."),

	USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
	BOOK_NOT_FOUND(NOT_FOUND,"없는 책입니다."),
	PURCHASE_HISTORY_NOT_FOUND(NOT_FOUND, "구매 이력이 존재하지 않습니다."),
	ORDER_NOT_FOUND(NOT_FOUND, "주문을 찾을 수 없습니다."),


	FILE_SIZE_LIMIT_EXCEEDED(PAYLOAD_TOO_LARGE, "전송하려는 개별 파일의 크기가 너무 큽니다."),
	SIZE_LIMIT_EXCEEDED(PAYLOAD_TOO_LARGE, "전송하려는 모든 파일의 크기가 너무 큽니다."),

	/* 5xx */
	CALL_ADMIN(INTERNAL_SERVER_ERROR,"어드민을 호출해주세요."),
	OPENAPI_ERROR(INTERNAL_SERVER_ERROR, "OPENAPI ERROR"),
	REDIS_ERROR(INTERNAL_SERVER_ERROR,"CACHE ERROR"),
	ERROR(INTERNAL_SERVER_ERROR,"알 수 없는 에러가 발생했습니다."),
	DB_AUTHENTICATE_ERROR(INTERNAL_SERVER_ERROR, "DB 인증에 실패했습니다."),
	DB_TOO_MANY_CONNECTION_ERROR(INTERNAL_SERVER_ERROR, "DB 연결이 너무 많습니다."),
	DEFAULT_DB_ERROR(INTERNAL_SERVER_ERROR, "데이터베이스 에러가 발생했습니다."),
	FILE_IO_FAILED(INTERNAL_SERVER_ERROR, "파일 입출력 중 에러가 발생했습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
