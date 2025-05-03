package xyz.tomorrowlearncamp.bookking.domain.user.auth.service;

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

import java.time.LocalDateTime;
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidRequestException(ErrorMessage.WRONG_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole());

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.flush();

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

        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByToken(refreshToken);

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
        validateEmail(request.getEmail());

        User user = User.of(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                UserRole.ROLE_USER,
                request.getAddress(),
                request.getGender(),
                request.getAge(),
                request.getNickname()
        );

        User saveUser = userRepository.save(user);
        return SignupResponse.of(saveUser);
    }

    @Transactional(readOnly = true)
    public void validateEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new InvalidRequestException(ErrorMessage.EMAIL_DUPLICATED);
        }
    }
}
