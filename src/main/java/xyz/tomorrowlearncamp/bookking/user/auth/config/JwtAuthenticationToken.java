package xyz.tomorrowlearncamp.bookking.user.auth.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.AuthUser;

/**
 * security 인증 토큰
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthUser authUser;

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
}
