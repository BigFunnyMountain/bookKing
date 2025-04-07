package xyz.tomorrowlearncamp.bookking.domain.user.auth.dto;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
public class SignupResponse {
    private final Long userId;
    private final String email;

    private SignupResponse(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public static SignupResponse of(User user) {
        return new SignupResponse(user.getId(), user.getEmail());
    }
}
