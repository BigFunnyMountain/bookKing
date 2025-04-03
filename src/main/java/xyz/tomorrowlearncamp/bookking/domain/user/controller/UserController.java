package xyz.tomorrowlearncamp.bookking.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.UpdateUserRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/v1/users/myInfo")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal AuthUser authUser) {
        UserResponse response = userService.getMyInfo(authUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/v1/users/myInfo")
    public ResponseEntity<UserResponse> updateMyInfo(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse updatingUser = userService.updateUser(authUser.getUserId(), updateUserRequest);
        return ResponseEntity.ok(updatingUser);

    }
}
