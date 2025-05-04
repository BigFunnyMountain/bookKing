package xyz.tomorrowlearncamp.bookking.domain.user.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.common.util.JwtProvider;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AccessTokenResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.entity.RefreshToken;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.repository.RefreshTokenRepository;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.LoginRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.SignInResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignInResponse signin(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRequestException(ErrorMessage.WRONG_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole());

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(user.getId());
        tokens.forEach(RefreshToken::softDelete);


        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiredAt(LocalDateTime.now().plusDays(14))
                        .build()
        );
        return SignInResponse.of(user, accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AccessTokenResponse refreshAccessToken(String refreshToken) {
        // 값 넘어온거 확인
        log.info("======== [refresh] 클라이언트로부터 받은 refreshToken: {}", refreshToken);

        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByTokenAndDeletedFalse(refreshToken);

        if (optionalToken.isEmpty()) {
            log.warn("======== [refresh] DB에서 일치하는 RefreshToken을 찾지 못했음. 요청 토큰: {}", refreshToken);
            throw new InvalidRequestException(ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        RefreshToken token = optionalToken.get();
        //찾고 로그
        log.info("========= [refresh] db 에서 찾은 refreshToken: {}", token.getToken());
        log.info("======== [refresh] 토큰 만료 시간: {}", token.getExpiredAt());

        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            log.warn("======== [refresh] 만료된 Refresh Token. expiredAt: {}", token.getExpiredAt());
            throw new InvalidRequestException(ErrorMessage.EXPIRED_REFRESH_TOKEN);
        }

        User user = userRepository.findById(token.getUserId())
                .filter(u -> !u.isDeleted()) // delete된 유저가 refresh 토큰으로 accessToken을 재발급받는 거 막음
                .orElseThrow(() -> {
                    log.warn("======== [refresh] userId={} 에 해당하는 유저를 찾지 못했습니다.", token.getUserId());
                    return new NotFoundException(ErrorMessage.USER_NOT_FOUND);
                });

        log.info("======== [refresh] AccessToken 재발급 성공 (userId={}, email={})", user.getId(), user.getEmail());

        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());

        return AccessTokenResponse.of(newAccessToken);
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException(ErrorMessage.EMAIL_DUPLICATED);
        }

        User user = User.of(
                request,
                passwordEncoder.encode(request.getPassword()),
                UserRole.ROLE_USER
        );

        User saveUser = userRepository.save(user);
        return SignupResponse.of(saveUser);
    }

    @Transactional(readOnly = true)
    public void validateEmail(String email) {

        if (userRepository.existsByEmailAndDeletedFalse(email)) {
            throw new IllegalArgumentException("이미 가입된 email 입니다.");
        }
    }
}
