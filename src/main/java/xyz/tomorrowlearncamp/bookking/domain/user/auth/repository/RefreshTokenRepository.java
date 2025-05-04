package xyz.tomorrowlearncamp.bookking.domain.user.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.entity.RefreshToken;

import java.util.List;
import java.util.Optional;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
    Optional<RefreshToken> findByTokenAndDeletedFalse(String token);
    List<RefreshToken> findAllByUserId(Long userId);
}