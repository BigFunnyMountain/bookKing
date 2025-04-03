package xyz.tomorrowlearncamp.bookking.user.dto;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.user.entity.User;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
public class LoginResponse {
    private final Long userId;
    private final String email;
    private String name;
    private String nickname;
    private String role;
    private String accessToken;

    private String refreshToken;
    private LoginResponse(Long userId, String email, String name, String nickname, String role, String accessToken, String refreshToken) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse of(User user, String accessToken, String refreshToken) {
        return new LoginResponse(
                user.getId(), user.getEmail(),user.getName(),user.getNickname(),user.getRole().name(), accessToken, refreshToken);
    }
}
