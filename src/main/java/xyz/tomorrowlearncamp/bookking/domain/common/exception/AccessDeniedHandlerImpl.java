package xyz.tomorrowlearncamp.bookking.domain.common.exception;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;

@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.error("Access Denied: {}", accessDeniedException.getMessage());


		response.sendError(HttpServletResponse.SC_FORBIDDEN, ErrorMessage.FORBIDDEN_USER.getMessage());
	}
}
