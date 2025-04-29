package xyz.tomorrowlearncamp.bookking.domain.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse<T> implements Response<T> {

	private HttpStatus status;
	private final T error;

	public ErrorResponse(T error) {
		this.error = error;
	}

	public ErrorResponse(HttpStatus status, T error) {
		this.status = status;
		this.error = error;
	}

	@Override
	public T getData() {
		return null;
	}
}
