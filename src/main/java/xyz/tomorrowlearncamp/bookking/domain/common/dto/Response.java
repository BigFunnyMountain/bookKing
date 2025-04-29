package xyz.tomorrowlearncamp.bookking.domain.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import org.springframework.http.HttpStatus;

@JsonInclude(Include.NON_NULL)
public interface Response<T> {

	static <T> Response<T> success(T data) {
		return new SuccessResponse<>(data);
	}

	static <T> Response<T> fail(HttpStatus status, T error) {
		return new ErrorResponse<>(status, error);
	}

	static <T> Response<List<T>> fail(HttpStatus status, List<T> error) {
		return new ErrorResponse<>(status, error);
	}

	T getData();

	T getError();
}
