package xyz.tomorrowlearncamp.bookking.domain.user.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

import java.security.Key;
import java.security.Signature;
import java.util.Base64;
import java.util.Date;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Slf4j
@Component
public class JwtProvider {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30;
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 14;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] decodingKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodingKey);
    }

    private String generateToken(Long userId, String email, UserRole role, long validMillis) {
        Date now = new Date();
        Date end = new Date(now.getTime() + validMillis);

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role.name())
                .setIssuedAt(now)
                .setExpiration(end)
                .signWith(key, algorithm)
                .compact();
    }

    public String createAccessToken(Long userId, String email, UserRole userRole) {
        return generateToken(userId, email, userRole, ACCESS_TOKEN_VALIDITY);
    }

    public String createRefreshToken(Long userId, String email, UserRole userRole) {
        return generateToken(userId, email, userRole, REFRESH_TOKEN_VALIDITY);
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(removeBearerPrefix(token))
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰입니다\t{}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("잘못된 Jwt 입니다\t{}", e.getMessage());
            throw e;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String removeBearerPrefix(String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }

}
