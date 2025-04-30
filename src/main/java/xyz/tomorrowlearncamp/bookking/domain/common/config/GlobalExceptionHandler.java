package xyz.tomorrowlearncamp.bookking.domain.common.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.*;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public Response<CustomExceptionDto> invalidRequestExHandler(
		final InvalidRequestException ex,
		HttpServletResponse response
	) {
		log.error("[InvalidRequestException] {}로 인한 예외 발생", ex.getErrorMessage().name(), ex.getCause());
		HttpStatus status = ex.getErrorMessage().getStatus();
		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(ServerException.class)
	public Response<CustomExceptionDto> serverExHandler(
		final ServerException ex,
		HttpServletResponse response
	) {
		log.error("[ServerException] {}로 인한 예외 발생", ex.getErrorMessage().name(), ex.getCause());
		HttpStatus status = ex.getErrorMessage().getStatus();
		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public Response<CustomExceptionDto> notFoundExHandler(
		final NotFoundException ex,
		HttpServletResponse response
	) {
		log.error("[NotFoundException] {}로 인한 예외 발생", ex.getErrorMessage().name(), ex.getCause());
		HttpStatus status = ex.getErrorMessage().getStatus();
		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(ForbiddenRequestException.class)
	public Response<CustomExceptionDto> forbiddenExHandler(
			final ForbiddenRequestException ex,
			HttpServletResponse response
	) {
		log.error("[ForbiddenRequestException] {}로 인한 예외 발생", ex.getErrorMessage().name(), ex.getCause());
		HttpStatus status = ex.getErrorMessage().getStatus();
		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Response<List<ValidationExceptionDto>> methodArgumentNotValidExHandler(
		final MethodArgumentNotValidException ex,
		HttpServletResponse response
	) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ValidationExceptionDto> validationList = fieldErrors.stream()
			.map(fieldError -> {
				String code = fieldError.getCode();
				String field = fieldError.getField();
				String defaultMessage = fieldError.getDefaultMessage();
				return new ValidationExceptionDto(code, field, defaultMessage);
			}).toList();
		return Response.fail(HttpStatus.BAD_REQUEST, validationList);
	}
}
