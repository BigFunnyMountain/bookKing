package xyz.tomorrowlearncamp.bookking.domain.common.config;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.reactive.function.client.WebClientException;
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.CustomExceptionDto;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.ServerException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.ValidationExceptionDto;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public Response<CustomExceptionDto> invalidRequestExHandler(
		final InvalidRequestException ex,
		HttpServletResponse response
	) {
		HttpStatus status = ex.getErrorMessage().getStatus();

		log.error("[InvalidRequestException] name: {}, stackTrace: {}", ex.getErrorMessage().name(),
			ex.getStackTrace());

		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(ServerException.class)
	public Response<CustomExceptionDto> serverExHandler(
		final ServerException ex,
		HttpServletResponse response
	) {
		HttpStatus status = ex.getErrorMessage().getStatus();

		log.error("[ServerException] name: {}, stackTrace: {}", ex.getErrorMessage().name(), ex.getStackTrace());

		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public Response<CustomExceptionDto> notFoundExHandler(
		final NotFoundException ex,
		HttpServletResponse response
	) {
		HttpStatus status = ex.getErrorMessage().getStatus();

		log.error("[NotFoundException] name: {}, stackTrace:{}", ex.getErrorMessage().name(), ex.getStackTrace());

		response.setStatus(status.value());
		return Response.fail(status, new CustomExceptionDto(ex.getErrorMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Response<List<ValidationExceptionDto>> methodArgumentNotValidExHandler(
		final MethodArgumentNotValidException ex,
		HttpServletResponse response
	) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ValidationExceptionDto> validationList = fieldErrors.stream()
			.map(fieldError -> {
				String code = fieldError.getCode();
				String field = fieldError.getField();
				String defaultMessage = fieldError.getDefaultMessage();
				return new ValidationExceptionDto(code, field, defaultMessage);
			}).toList();

		log.error("[MethodArgumentNotValidException] stackTrace: {}", (Object[]) ex.getStackTrace());

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return Response.fail(HttpStatus.BAD_REQUEST, validationList);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Response<CustomExceptionDto> methodArgumentTypeMismatchExHandler(
		final MethodArgumentTypeMismatchException ex,
		HttpServletResponse response
	) {
		String expectedType = ex.getRequiredType() == null ? "Unknown" : ex.getRequiredType().getSimpleName();
		ErrorMessage typeMismatch = ErrorMessage.TYPE_MISMATCH;

		log.error("[MethodArgumentTypeMismatchException] field: {}, expected: {}, value: {}, stackTrace: {}",
			ex.getName(), expectedType, ex.getValue(), ex.getStackTrace());

		response.setStatus(typeMismatch.getStatus().value());
		return Response.fail(typeMismatch.getStatus(), new CustomExceptionDto(typeMismatch));
	}

	@ExceptionHandler(DataAccessException.class)
	public Response<CustomExceptionDto> dataAccessExHandler(
		final DataAccessException ex,
		HttpServletResponse response
	) {
		int sqlErrorCode = -1;
		String sqlExceptionMessage = "";
		String sqlState = "";
		ErrorMessage errorMessage;

		Throwable rootCause = ex.getRootCause();
		if (rootCause instanceof SQLException sqlException) {
			sqlErrorCode = sqlException.getErrorCode();
			sqlState = sqlException.getSQLState();
			sqlExceptionMessage = sqlException.getMessage();
		}

		switch (sqlErrorCode) {
			case 1040 -> errorMessage = ErrorMessage.DB_TOO_MANY_CONNECTION_ERROR;
			case 1045 -> errorMessage = ErrorMessage.DB_AUTHENTICATE_ERROR;
			default -> errorMessage = ErrorMessage.DEFAULT_DB_ERROR;
		}

		log.error("[DataAccessException] errorCode: {}, sqlState: {}, sqlExceptionMessage: {}, stackTrace: {}",
			sqlErrorCode, sqlState, sqlExceptionMessage, ex.getStackTrace());

		response.setStatus(errorMessage.getStatus().value());
		return Response.fail(errorMessage.getStatus(), new CustomExceptionDto(errorMessage));
	}

	@ExceptionHandler(WebClientException.class)
	public Response<CustomExceptionDto> webClientExHandler(
		final WebClientException ex,
		HttpServletResponse response
	) {
		ErrorMessage errorMessage = ErrorMessage.OPENAPI_ERROR;

		Throwable throwable = ex.getRootCause();
		if (throwable instanceof WebClientException webClientException) {
			log.error("[WebClientException] message: {}, stackTrace: {}", (Object[])webClientException.getStackTrace());
		}

		response.setStatus(errorMessage.getStatus().value());
		return Response.fail(errorMessage.getStatus(), new CustomExceptionDto(errorMessage));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public Response<CustomExceptionDto> maxUploadSizeExceededExHandler(
		final MaxUploadSizeExceededException ex,
		HttpServletResponse response
	) {
		ErrorMessage errorMessage = ErrorMessage.FILE_SIZE_LIMIT_EXCEEDED;
		log.error("[MaxUploadSizeExceededException] stackTrace: {}", (Object[])ex.getStackTrace());

		response.setStatus(errorMessage.getStatus().value());
		return Response.fail(errorMessage.getStatus(), new CustomExceptionDto(errorMessage));
	}

	@ExceptionHandler(SizeLimitExceededException.class)
	public Response<CustomExceptionDto> sizeLimitExceededExHandler(
		final SizeLimitExceededException ex,
		HttpServletResponse response
	) {
		ErrorMessage errorMessage = ErrorMessage.SIZE_LIMIT_EXCEEDED;
		log.error("[SizeLimitExceededException] actualSize: {}, stackTrace: {}", ex.getActualSize(), ex.getStackTrace());

		response.setStatus(errorMessage.getStatus().value());
		return Response.fail(errorMessage.getStatus(), new CustomExceptionDto(errorMessage));
	}

	@ExceptionHandler(IOException.class)
	public Response<CustomExceptionDto> ioExHandler(
		final IOException ex,
		HttpServletResponse response
	) {
		ErrorMessage errorMessage = ErrorMessage.FILE_IO_FAILED;
		log.error("[IOException] stackTrace: {}", (Object[])ex.getStackTrace());

		response.setStatus(errorMessage.getStatus().value());
		return Response.fail(errorMessage.getStatus(), new CustomExceptionDto(errorMessage));
	}

	@ExceptionHandler(Exception.class)
	public Response<CustomExceptionDto> exHandler(
		final Exception ex,
		HttpServletResponse response
	) {
		ErrorMessage errorMessage = ErrorMessage.ERROR;
		log.error("[Exception] stackTrace: {}", (Object[])ex.getStackTrace());

		response.setStatus(errorMessage.getStatus().value());
		return Response.fail(errorMessage.getStatus(), new CustomExceptionDto(errorMessage));
	}

}
