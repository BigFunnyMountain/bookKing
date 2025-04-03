package xyz.tomorrowlearncamp.bookking.user.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.user.auth.config.JwtProvider;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.RefreshTokenResponse;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.SignupResponse;
import xyz.tomorrowlearncamp.bookking.user.auth.entity.RefreshToken;
import xyz.tomorrowlearncamp.bookking.user.auth.repository.RefreshTokenRepository;
import xyz.tomorrowlearncamp.bookking.user.dto.LoginRequest;
import xyz.tomorrowlearncamp.bookking.user.dto.LoginResponse;
import xyz.tomorrowlearncamp.bookking.user.entity.User;
import xyz.tomorrowlearncamp.bookking.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;

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
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole());

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.flush();

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(jwtProvider.removeBearerPrefix(refreshToken))
                        .expiredAt(LocalDateTime.now().plusDays(14))
                        .build()
        );
        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        return LoginResponse.of(user, accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        // 값 넘어온거 확인
        log.info(">>> [refresh] 클라이언트로부터 받은 refreshToken: {}", refreshToken);

        Optional<RefreshToken> optionalToken = refreshTokenRepository.findByToken(refreshToken);

        if (optionalToken.isEmpty()) {
            log.warn(">>> [refresh] DB에서 일치하는 RefreshToken을 찾지 못했음. 요청 토큰: {}", refreshToken);
            throw new IllegalArgumentException("유효하지 않은 Refresh Token.");
        }

        RefreshToken token = optionalToken.get();

        //찾고 로그
        log.info(">>> [refresh] db 에서 찾은 refreshToken: {}", token.getToken());
        log.info(">>> [refresh] 토큰 만료 시간: {}", token.getExpiredAt());

        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            log.warn(">>> [refresh] 만료된 Refresh Token. expiredAt: {}", token.getExpiredAt());
            throw new IllegalArgumentException("만료된 Refresh Token.");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> {
                    log.warn(">>> [refresh] userId={} 에 해당하는 유저를 찾지 못했습니다.", token.getUserId());
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });

        log.info(">>> [refresh] AccessToken 재발급 성공 (userId={}, email={})", user.getId(), user.getEmail());

        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());

        return RefreshTokenResponse.of(newAccessToken);
    }

//    추가 예정일 경우 추가
//    public void logout(String refreshToken) {
//        refreshTokenRepository.deleteByToken(refreshToken);
//    }

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
            throw new IllegalArgumentException("이미 가입된 email 입니다.");
        }
    }
}
