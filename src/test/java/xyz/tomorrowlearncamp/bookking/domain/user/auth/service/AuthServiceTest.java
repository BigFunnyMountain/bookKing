package xyz.tomorrowlearncamp.bookking.domain.user.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.config.JwtProvider;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.entity.RefreshToken;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.repository.RefreshTokenRepository;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.LoginRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("로그인_실패-존재하지_않는_사용자")
    void login_fail_userNotFound() {
        // given
        LoginRequest request = LoginRequest.of("notfound@email.com", "1234");
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> authService.login(request, response));

        // then
        assertThat(throwable)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

        verify(userRepository).findByEmail(request.getEmail());
        verifyNoInteractions(passwordEncoder, jwtProvider, refreshTokenRepository);
    }

    @Test
    @DisplayName("로그인_실패-비밀번_불일치")
    void login_fail_wrongPassword() {
        // given
        String email = "test@email.com";
        String inputPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        User user = User.of(email, encodedPassword, "홍길동", UserRole.ROLE_USER, "서울", Gender.valueOf("MALE"), 20, "길동이");
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(inputPassword, encodedPassword)).willReturn(false);

        LoginRequest request = LoginRequest.of(email, inputPassword);

        // when
        Throwable throwable = catchThrowable(() -> authService.login(request, response));

        // then
        assertThat(throwable)
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(inputPassword, encodedPassword);
        verifyNoInteractions(jwtProvider, refreshTokenRepository);
    }

    @Test
    @DisplayName("리프레시_실패-DB_해당_토큰_없음")
    void refresh_fail_tokenNotFound() {
        // given
        String refreshToken = "not_in_db_token";
        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> authService.refreshAccessToken(refreshToken));
        log.info(">>> [refresh_fail_tokenNotFound] 예외 메시지: {}", thrown.getMessage());

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 Refresh Token.");
    }

    @Test
    @DisplayName("리프레시_실패-만료된_토큰")
    void refresh_fail_expiredToken() {
        // given
        String refreshToken = "expired_token";

        RefreshToken token = RefreshToken.builder()
                .userId(1L)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().minusMinutes(1))
                .build();

        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.of(token));

        // when
        Throwable thrown = catchThrowable(() -> authService.refreshAccessToken(refreshToken));
        log.info(">>> [refresh_fail_expiredToken] 예외 메시지: {}", thrown.getMessage());

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("만료된 Refresh Token.");
    }

    @Test
    @DisplayName("리프레시_실패-유저_없음")
    void refresh_fail_userNotFound() {
        // given
        String refreshToken = "valid_token";

        RefreshToken token = RefreshToken.builder()
                .userId(999L)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        setField(token, "id", 1L);
        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.of(token));
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> authService.refreshAccessToken(refreshToken));
        log.info(">>> [refresh_fail_userNotFound] 예외 메시지: {}", thrown.getMessage());

        // then
        assertThat(thrown)
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("회원가입_실패-이미_존재하는_이메일")
    void signup_fail_duplicateEmail() {
        // given
        String email = "test@email.com";
        SignupRequest request = SignupRequest.builder()
                .email(email)
                .password("1234")
                .name("홍길동")
                .address("서울")
                .gender(Gender.valueOf("MALE"))
                .age(25)
                .nickname("길동이")
                .build();

        given(userRepository.existsByEmail(email)).willReturn(true);

        // when
        Throwable thrown = catchThrowable(() -> authService.signup(request));
        log.info(">>> [signup_fail_duplicateEmail] 예외 메시지: {}", thrown.getMessage());

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 email 입니다.");

        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, jwtProvider, refreshTokenRepository);
    }

    @Test
    void validateEmail() {
    }
}