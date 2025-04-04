package xyz.tomorrowlearncamp.bookking.domain.user.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.service.AuthService;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.config.JwtProvider;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.RefreshTokenResponse;
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
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/v1/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request, response);
        System.out.println("응답 내용 : " + loginResponse.getAccessToken());
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/v1/auth/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(HttpServletRequest request,
                                                        HttpServletResponse response) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh Token이 누락되었거나 잘못된 형식입니다.");
        }

        String refreshToken = jwtProvider.removeBearerPrefix(header);
        RefreshTokenResponse newTokenResponse = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(newTokenResponse);
    }
}

