package xyz.tomorrowlearncamp.bookking.domain.user.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.config.JwtProvider;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AccessTokenResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.service.AuthService;
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
    public Response<AccessTokenResponse> refresh(HttpServletRequest request,
                                                       HttpServletResponse response) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh Token이 누락되었거나 잘못된 형식입니다.");
        }

        String refreshToken = jwtProvider.removeBearerPrefix(header);
        AccessTokenResponse newTokenResponse = authService.refreshAccessToken(refreshToken);

        return Response.success(newTokenResponse);
    }
}

