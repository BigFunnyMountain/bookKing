package xyz.tomorrowlearncamp.bookking.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.UpdateUserRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                ));
        return UserResponse.of(user);
    }

    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        user.update(updateUserRequest.getNickname(), updateUserRequest.getAddress());
        userRepository.save(user);

        return UserResponse.of(user);
    }
}