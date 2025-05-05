package xyz.tomorrowlearncamp.bookking.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Builder
    private LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginRequest of(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
