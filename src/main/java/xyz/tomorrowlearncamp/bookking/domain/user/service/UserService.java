package xyz.tomorrowlearncamp.bookking.domain.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.common.util.S3Upload;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.UpdateUserRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.request.UpdateUserRoleRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.io.IOException;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Upload s3Upload;

    @Transactional
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        user.updateUserInfo(updateUserRequest.getNickname(), updateUserRequest.getAddress());
        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateUserRoleRequest updateUserRoleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
        UserRole updateRole = UserRole.of(updateUserRoleRequest.getRole());

        if (user.getRole().equals(updateRole)) {
            throw new InvalidRequestException(ErrorMessage.ROLE_CHANGE_NOT_ALLOWED);
        }
        user.updateRole(updateRole);
        return UserResponse.of(user);
    }

    @Transactional
    public void deleteUser(Long userId, String checkPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(checkPassword, user.getPassword())) {
            throw new NotFoundException(ErrorMessage.WRONG_PASSWORD);
        }
        userRepository.delete(user);
    }

    @Transactional
    public String updateProfileImage(Long userId, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        String imageUrl = s3Upload.uploadProfileImage(image);
        user.updateProfileImageUrl(imageUrl);

        return imageUrl;
    }
}