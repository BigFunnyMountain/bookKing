package xyz.tomorrowlearncamp.bookking.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_USER(Authority.USER),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    public static UserRole of(String userRole) {
        String roleChange = userRole.startsWith("ROLE_") ? userRole : "ROLE_" + userRole;

        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(roleChange))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole 입니다."));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
