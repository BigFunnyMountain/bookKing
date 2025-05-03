package xyz.tomorrowlearncamp.bookking.common.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import xyz.tomorrowlearncamp.bookking.common.entity.AuthUser;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final transient AuthUser authUser;

    private JwtAuthenticationToken(AuthUser authUser) {
        super(authUser.getAuthorities());
        this.authUser = authUser;
        setAuthenticated(true);
    }

    public static JwtAuthenticationToken of(AuthUser authUser) {
        return new JwtAuthenticationToken(authUser);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authUser;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JwtAuthenticationToken && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
