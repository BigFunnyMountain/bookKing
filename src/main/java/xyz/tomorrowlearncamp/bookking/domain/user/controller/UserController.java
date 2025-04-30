package xyz.tomorrowlearncamp.bookking.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.DeleteUserRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.UpdateUserRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.UpdateUserRoleRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/v1/users/myInfo")
    public Response<UserResponse> getMyInfo(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse response = userService.getMyInfo(authUser.getUserId());
        return Response.success(response);
    }

    @PatchMapping("/v1/users/myInfo")
    public Response<UserResponse> updateMyInfo(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse updatingUser = userService.updateUser(authUser.getUserId(), updateUserRequest);
        return Response.success(updatingUser);
    }

    @PatchMapping("/v1/users/{userId}/role")
    public Response<UserResponse> updateUserRole(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest) {

        if (!authUser.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "경고 ! 본인 계정의 권한만 변경하실 수 있습니다.");
        }

        UserResponse updateUser = userService.updateUserRole(userId, updateUserRoleRequest);
        return Response.success(updateUser);
    }

    @DeleteMapping("/v1/users/{userId}")
    public Response<String> deleteUser(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long userId,
            @Valid @RequestBody DeleteUserRequest deleteUserRequest) {

        userService.deleteUser(userId, authUser.getUserId(), deleteUserRequest.getPassword());
        return Response.success("회원 탈퇴가 정상적으로 처리되었습니다");
    }

    @PostMapping("/v1/users/profile-image")
    public Response<String> uploadProfileImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam("image") MultipartFile image) throws IOException {

        String imageUrl = userService.updateProfileImage(authUser.getUserId(), image);
        return Response.success(imageUrl);
    }
}
