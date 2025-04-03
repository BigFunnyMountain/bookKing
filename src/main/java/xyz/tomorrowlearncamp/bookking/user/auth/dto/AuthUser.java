package xyz.tomorrowlearncamp.bookking.user.auth.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import xyz.tomorrowlearncamp.bookking.user.enums.UserRole;

import java.util.Collection;
import java.util.List;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 * 사용자 정보 보관용
 */
@Getter
public class AuthUser {
    private final Long userId;
    private final String email;
    private final List<GrantedAuthority> authorities;


    private AuthUser(Long userId, String email, List<GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

    public static AuthUser of(Long userId, String email, UserRole userRole) {
        return new AuthUser(
                userId,
                email,
                List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))

        );
    }
}
