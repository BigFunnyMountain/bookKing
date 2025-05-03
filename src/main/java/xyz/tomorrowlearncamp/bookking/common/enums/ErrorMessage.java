package xyz.tomorrowlearncamp.bookking.common.enums;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

	/* 3xx */
	EXPIRED_JWT_TOKEN(MOVED_PERMANENTLY, "만료된 JWT token 입니다."),
	RESIGNIN(MOVED_PERMANENTLY, "다시 로그인 해주세요."),

	/* 4xx */
	ZERO_BOOK_STOCK(BAD_REQUEST,"남은 책이 없습니다."),
	SHORT_ON_MONEY(BAD_REQUEST,"돈이 부족합니다."),
	REVIEW_ALREADY_WRITTEN(BAD_REQUEST, "이미 작성된 리뷰입니다."),
	TYPE_MISMATCH(BAD_REQUEST, "올바른 값을 입력해주세요."),
	INVALID_JWT_SIGNATURE(BAD_REQUEST,"유효하지 않는 JWT 서명 입니다."),
	UNSUPPORTED_JWT_TOKEN(BAD_REQUEST, "지원되지 않는 JWT 토큰 입니다."),



    NO_AUTHORITY_TO_WRITE_A_REVIEW(FORBIDDEN, "리뷰를 작성할 권한이 없습니다."),
    INVALID_HEADER(BAD_REQUEST, "Authorization 헤더가 잘못되었습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "유효하지 않은 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "만료된 Refresh Token입니다."),
    EMAIL_DUPLICATED(BAD_REQUEST, "이미 가입된 이메일입니다."),
    WRONG_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ROLE_CHANGE_NOT_ALLOWED(BAD_REQUEST, "ROLE_USER만 ROLE_ADMIN으로 변경할 수 있습니다."),
    NO_AUTHORITY_TO_CHANGE_ROLE(FORBIDDEN, "본인 계정만 권한을 변경할 수 있습니다."),
    NO_AUTHORITY_TO_DELETE_USER(FORBIDDEN, "본인 계정만 탈퇴할 수 있습니다."),
	NO_AUTHORITY_TO_RETURN_A_PAYMENT(FORBIDDEN, "환불할 권한이 없습니다."),
	FORBIDDEN_ADMINISTRATOR(FORBIDDEN, "권한이 없습니다."),
	FORBIDDEN_USER(FORBIDDEN, "권한이 없습니다."),

	USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
	BOOK_NOT_FOUND(NOT_FOUND,"없는 책입니다."),
	PURCHASE_HISTORY_NOT_FOUND(NOT_FOUND, "구매 이력이 존재하지 않습니다."),
	ORDER_NOT_FOUND(NOT_FOUND, "주문을 찾을 수 없습니다."),
	NO_HANDLER_FOUND(NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),


	FILE_SIZE_LIMIT_EXCEEDED(PAYLOAD_TOO_LARGE, "전송하려는 개별 파일의 크기가 너무 큽니다."),
	SIZE_LIMIT_EXCEEDED(PAYLOAD_TOO_LARGE, "전송하려는 모든 파일의 크기가 너무 큽니다."),

	/* 5xx */
	CALL_ADMIN(INTERNAL_SERVER_ERROR,"어드민을 호출해주세요."),
	OPENAPI_ERROR(INTERNAL_SERVER_ERROR, "OPENAPI ERROR"),
	ELASTICSEARCH_ERROR(INTERNAL_SERVER_ERROR, "ELASTICSEARCH ERROR"),
	REDIS_ERROR(INTERNAL_SERVER_ERROR,"CACHE ERROR"),
	ERROR(INTERNAL_SERVER_ERROR,"알 수 없는 에러가 발생했습니다."),
	DB_AUTHENTICATE_ERROR(INTERNAL_SERVER_ERROR, "DB 인증에 실패했습니다."),
	DB_TOO_MANY_CONNECTION_ERROR(INTERNAL_SERVER_ERROR, "DB 연결이 너무 많습니다."),
	DEFAULT_DB_ERROR(INTERNAL_SERVER_ERROR, "데이터베이스 에러가 발생했습니다."),
	FILE_IO_FAILED(INTERNAL_SERVER_ERROR, "파일 입출력 중 에러가 발생했습니다."),
	INDEX_FAILED_ERROR(INTERNAL_SERVER_ERROR, "INDEX 작업 중 에러가 발생했습니다."),
	REINDEXING_IO_ERROR(INTERNAL_SERVER_ERROR, "REINDEX 작업 수행 중 에러가 발생했습니다."),
	AWS_S3_ERROR(INTERNAL_SERVER_ERROR, "AWS S3 업로드 중 에러가 발생했습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
