package xyz.tomorrowlearncamp.bookking.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.tomorrowlearncamp.bookking.common.dto.CustomExceptionDto;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.enums.RedirectionMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.common.util.JwtProvider;
import xyz.tomorrowlearncamp.bookking.common.entity.AuthUser;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AccessTokenResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.entity.RefreshToken;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.repository.RefreshTokenRepository;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	private final RefreshTokenRepository refreshTokenRepository;

	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws IOException, ServletException {
		String authHeader = request.getHeader("Authorization");

		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = jwtProvider.removeBearerPrefix(authHeader);
				Claims claims = jwtProvider.extractClaims(token);
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					setAuthentication(claims);
				}
			}

		} catch (SecurityException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendError(response, ErrorMessage.INVALID_JWT_SIGNATURE);
			return;
		} catch (ExpiredJwtException e) {
			handleExpiredToken( request, response);
			return;
		} catch (UnsupportedJwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendError(response, ErrorMessage.UNSUPPORTED_JWT_TOKEN);
			return;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendError(response, ErrorMessage.ERROR);
			return;
		}
		filterChain.doFilter(request, response);
	}

	private void setAuthentication(Claims claims) {
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		UserRole userRole = UserRole.of(claims.get("role", String.class));

		AuthUser authUser = AuthUser.of(userId, email, userRole);
		JwtAuthenticationToken authenticationToken = JwtAuthenticationToken.of(authUser);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	private void sendError(HttpServletResponse response, ErrorMessage error) throws IOException {
		log.error(error.name() + " : ", error.getMessage() );

		CustomExceptionDto ex = new CustomExceptionDto(error);
		ObjectMapper objectMapper = new ObjectMapper();
		String errorJson = objectMapper.writeValueAsString(Response.fail(error.getStatus(), ex));

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(errorJson);
	}

	private void sendRedirect(HttpServletResponse response, RedirectionMessage message) throws IOException {
		log.info(message.name() + " : ", message.getMessage() );

		ObjectMapper objectMapper = new ObjectMapper();
		String redirect = objectMapper.writeValueAsString(Response.success(message.getStatus()));

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(redirect);
	}

	private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("accessToken 재발급");
		String refreshToken = request.getHeader("refreshToken");

		// 엑세스토큰이 만료가 됬고 ( 엑세스 토큰 있음 ) , 리프레쉬 토큰이 없는 경우
		if (refreshToken == null) {
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			sendRedirect(response, RedirectionMessage.EXPIRED_JWT_ACCESS_TOKEN);
			return;
		}

		RefreshToken getToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
			() -> new InvalidRequestException(ErrorMessage.INVALID_REFRESH_TOKEN)
		);
		if( getToken.getExpiredAt().isBefore(java.time.LocalDateTime.now()) ) {
			throw new InvalidRequestException(ErrorMessage.EXPIRED_JWT_REFRESH_TOKEN);
		}
		if( !getToken.getToken().equals(refreshToken) ) {
			throw new InvalidRequestException(ErrorMessage.INVALID_REFRESH_TOKEN);
		}

		User getUser = userRepository.findById(getToken.getUserId()).orElseThrow(
			() -> new InvalidRequestException(ErrorMessage.USER_NOT_FOUND)
		);

		String accessToken = jwtProvider.createAccessToken(getUser.getId(), getUser.getEmail(), getUser.getRole());

		ObjectMapper objectMapper = new ObjectMapper();
		String returnToken = objectMapper.writeValueAsString(Response.success(AccessTokenResponse.of(accessToken)));

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(returnToken);
	}
}
