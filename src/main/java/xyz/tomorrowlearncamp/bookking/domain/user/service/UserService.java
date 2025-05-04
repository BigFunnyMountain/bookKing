package xyz.tomorrowlearncamp.bookking.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import xyz.tomorrowlearncamp.bookking.domain.user.aws.S3Upload;
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

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (user.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "탈퇴한 사용자입니다.");
        }

        return UserResponse.of(user);
    }


    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        user.updateUserInfo(updateUserRequest.getNickname(), updateUserRequest.getAddress());

        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateUserRoleRequest updateUserRoleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"));

        UserRole currentRole = user.getRole();
        UserRole requestedRole = UserRole.of(updateUserRoleRequest.getRole());

        if (currentRole == UserRole.ROLE_USER && requestedRole == UserRole.ROLE_ADMIN) {
            user.updateRole(UserRole.ROLE_ADMIN);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한 변경이 허용되지 않습니다. \n ROLE_USER만 ROLE_ADMIN으로 변경할 수 있습니다.");
        }

        return UserResponse.of(user);
    }

    @Transactional
    public void deleteUser(Long userId, Long loginUserId, String checkPassword) {
        if (!userId.equals(loginUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 게정만 탈퇴가 가능합니다");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(checkPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다");
        }
        user.softDelete();
    }

    @Transactional
    public String updateProfileImage(Long userId, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String imageUrl = s3Upload.uploadProfileImage(image);
        user.updateProfileImageUrl(imageUrl);

        return imageUrl;
    }
}