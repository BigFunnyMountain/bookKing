package xyz.tomorrowlearncamp.bookking.domain.user.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.tomorrowlearncamp.bookking.common.util.JwtProvider;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AccessTokenResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.LoginRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.LoginResponse;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/v1/auth/signup")
    public Response<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return Response.success(authService.signup(request));
    }

    @PostMapping("/v1/auth/signin")
    public Response<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        return Response.success(authService.login(request, response));
    }

    @PostMapping("/v1/auth/refresh")
    public Response<AccessTokenResponse> refresh(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidRequestException(ErrorMessage.INVALID_HEADER);
        }

        String refreshToken = jwtProvider.removeBearerPrefix(header);
        AccessTokenResponse newTokenResponse = authService.refreshAccessToken(refreshToken);

        return Response.success(newTokenResponse);
    }
}

