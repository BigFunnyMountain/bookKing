package xyz.tomorrowlearncamp.bookking.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.user.aws.S3Upload;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.UpdateUserRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.UpdateUserRoleRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Upload s3Upload;

    private User user;

    @BeforeEach
    void setup() {
        user = User.of(
                "test@email.com",
                "encodedPassword",
                "테스터",
                UserRole.ROLE_USER,
                "한국",
                Gender.MALE,
                25,
                "tester"
        );
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    @DisplayName("내_정보_조회_성공")
    void getMyInfo_success() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        var result = userService.getMyInfo(userId);

        // then
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("내_정보_조회_실패-사용자_없음")
    void getMyInfo_fail_userNotFound() {
        // given
        Long userId = -1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> userService.getMyInfo(userId));

        // then
        assertThat(throwable)
                .isInstanceOf(NotFoundException.class);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원정보_수정_성공")
    void updateUser_success() {
        // given
        Long userId = 1L;
        String newNickname = "바뀐닉넴";
        String newAddress = "제주도";

        UpdateUserRequest request = new UpdateUserRequest(newNickname, newAddress);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponse userResponse = userService.updateUser(userId, request);

        // then
        assertThat(userResponse.getNickname()).isEqualTo(newNickname);
        assertThat(userResponse.getAddress()).isEqualTo(newAddress);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("회원정보_수정_실패-없는_사용자")
    void updateUser_fail_userNotFound() {
        // given
        Long userId = -1L;
        UpdateUserRequest request = new UpdateUserRequest("바뀐닉넴", "제주도");

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when && then
        NotFoundException assertThrows = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, request));

        assertInstanceOf(NotFoundException.class, assertThrows);
        assertEquals(ErrorMessage.USER_NOT_FOUND, assertThrows.getErrorMessage());
    }

    @Test
    @DisplayName("회원_권한_변경_성공-ROLE_USER_에서_ROLE_ADMIN으로_변경")
    void updateUserRole_success() {
        // given
        Long userId = 1L;
        UpdateUserRoleRequest request = new UpdateUserRoleRequest("ROLE_ADMIN");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        var result = userService.updateUserRole(userId, request);

        // then
        assertThat(result.getRole().toString()).isEqualTo("ROLE_ADMIN");
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("회원_권한_변경_실패-없는_사용자")
    void updateUserRole_fail_userNotFound() {
        // given
        Long userId = -1L;
        UpdateUserRoleRequest request = new UpdateUserRoleRequest("ROLE_ADMIN");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when && then
        NotFoundException assertThrows = assertThrows(NotFoundException.class,
                () -> userService.updateUserRole(userId, request));

        assertInstanceOf(NotFoundException.class, assertThrows);
        assertEquals(ErrorMessage.USER_NOT_FOUND, assertThrows.getErrorMessage());
    }

    @Test
    @DisplayName("회원_권한_변경_실패-ROLE_USER_에서_ROLE_USER_로_변경_불가")
    void updateUserRole_fail_invalidChange() {
        // given
        Long userId = 1L;
        UpdateUserRoleRequest request = new UpdateUserRoleRequest("ROLE_USER");// 요청을 변경 할 필요가없음 user-> user니까.

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when && then
        InvalidRequestException assertThrows = assertThrows(InvalidRequestException.class,
                () -> userService.updateUserRole(userId, request));

        assertInstanceOf(InvalidRequestException.class, assertThrows);
        assertEquals(ErrorMessage.ROLE_CHANGE_NOT_ALLOWED, assertThrows.getErrorMessage());
    }

    @Test
    @DisplayName("회원_삭제_성공")
    void deleteUser_success() {
        // given
        Long userId = 1L;
        String password = "1234";

        ReflectionTestUtils.setField(user, "id", userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);

        // when
        userService.deleteUser(userId, password);

        // then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(userRepository).delete(user);
    }

}