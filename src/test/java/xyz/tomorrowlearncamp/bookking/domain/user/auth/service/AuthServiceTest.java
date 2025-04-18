package xyz.tomorrowlearncamp.bookking.domain.user.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
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

@ActiveProfiles("dev")
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
    @DisplayName("로그인_성공")
    void login_success() {
        //given
        String email = "test@email.com";
        String password = "1234";
        String encodedPassword = "encodedPassword";
        String accessToken = "Bearer access-token";
        String refreshToken = "Bearer refresh-token";
        Long userId = 1L;

        User user = User.of(email, encodedPassword, "홍길동", UserRole.ROLE_USER, "서울", Gender.MALE, 25, "길동이");
        setField(user, "id", userId);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(jwtProvider.createAccessToken(userId, email, user.getRole())).willReturn(accessToken);
        given(jwtProvider.createRefreshToken(userId, email, user.getRole())).willReturn(refreshToken);
        given(jwtProvider.removeBearerPrefix(refreshToken)).willReturn("refresh-token");

        LoginRequest request = LoginRequest.of(email, password);

        //when
        var result = authService.login(request, response);

        //then
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtProvider).createAccessToken(userId, email, user.getRole());
        verify(jwtProvider).createRefreshToken(userId, email, user.getRole());
        verify(jwtProvider).removeBearerPrefix(refreshToken);
        verify(refreshTokenRepository).deleteByUserId(userId);
        verify(refreshTokenRepository).flush();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(response).setHeader(HttpHeaders.AUTHORIZATION, accessToken);
    }


    @Test
    @DisplayName("리프레시_실패-DB_해당_토큰_없음")
    void refresh_fail_tokenNotFound() {
        // given
        String refreshToken = "not_in_db_token";
        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> authService.refreshAccessToken(refreshToken));
        log.info(">>>>>>>>>> [refresh_fail_tokenNotFound] 예외 메시지: {}", thrown.getMessage());

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
        log.info(">>>>>>>>> [refresh_fail_expiredToken] 예외 메시지: {}", thrown.getMessage());

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
        log.info(">>>>>>>> [refresh_fail_userNotFound] 예외 메시지: {}", thrown.getMessage());

        // then
        assertThat(thrown)
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("리프레시_성공-accessToken_재발급")
    void refresh_success() {
        //given
        String refreshToken = "valid_refresh_token";
        Long userId = 1L;
        String email = "test@email.com";
        UserRole role = UserRole.ROLE_USER;
        String newAccessToken = "Bearer new-access-token";

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .build();
        setField(token, "id", 1L);

        User user = User.of(email, "encodedPassword", "홍길동", role, "서울", Gender.MALE, 25, "길동이");
        setField(user, "id", userId);

        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.of(token));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(jwtProvider.createAccessToken(userId, email, role)).willReturn(newAccessToken);

        //when
        var result = authService.refreshAccessToken(refreshToken);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(newAccessToken);

        verify(refreshTokenRepository).findByToken(refreshToken);
        verify(userRepository).findById(userId);
        verify(jwtProvider).createAccessToken(userId, email, role);
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
        log.info(">>>>>>> [signup_fail_duplicateEmail] 예외 메시지: {}", thrown.getMessage());

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 email 입니다.");

        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, jwtProvider, refreshTokenRepository);
    }

    @Test
    @DisplayName("회원가입_성공")
    void signup_success() {
        // given
        String email = "test@email.com";
        String password = "1234";
        String encodedPassword = "encodedPassword";

        SignupRequest request = SignupRequest.builder()
                .email(email)
                .password(password)
                .name("홍길동")
                .address("서울")
                .gender(Gender.MALE)
                .age(25)
                .nickname("길동이")
                .build();

        User user = User.of(email, encodedPassword, "홍길동", UserRole.ROLE_USER, "서울", Gender.MALE, 25, "길동이");
        setField(user, "id", 1L);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        var result = authService.signup(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이메일_중복_확인_성공")
    void validateEmail_success() {
        // given
        String email = "newuser@email.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when
        Throwable throwable = catchThrowable(() -> authService.validateEmail(email));

        // then
        assertThat(throwable).doesNotThrowAnyException();
        verify(userRepository).existsByEmail(email);
    }
}