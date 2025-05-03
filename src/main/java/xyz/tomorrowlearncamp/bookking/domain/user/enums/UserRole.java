package xyz.tomorrowlearncamp.bookking.domain.user.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    public static UserRole of(String userRole) {
        String roleChange = userRole.startsWith("ROLE_") ? userRole : "ROLE_" + userRole;

        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(roleChange))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole 입니다."));
    }
}
