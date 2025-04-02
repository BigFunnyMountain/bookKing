package xyz.tomorrowlearncamp.bookking.domain.common.config;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import xyz.tomorrowlearncamp.bookking.domain.common.entity.ErrorResponse;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.ServerException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> invalidRequestExHandler(InvalidRequestException ex) {
		return getErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorResponse> serverExHandler(ServerException ex) {
		return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> notFoundExHandler(NotFoundException ex) {
		return getErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	private ResponseEntity<ErrorResponse> getErrorResponse(HttpStatus status, String message) {
		ErrorResponse response = new ErrorResponse(status.value(), message);
		return new ResponseEntity<>(response, status);
	}
}
