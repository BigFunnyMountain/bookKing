package xyz.tomorrowlearncamp.bookking.common.enums;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedirectionMessage {

	/* 3xx */
	EXPIRED_JWT_ACCESS_TOKEN(MOVED_PERMANENTLY, "만료된 JWT Access Token 입니다.")
	;

	private final HttpStatus status;
	private final String message;
}
