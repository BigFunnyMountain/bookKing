package xyz.tomorrowlearncamp.bookking.domain.user.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.entity.RefreshToken;

import java.util.List;
import java.util.Optional;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndDeletedAtIsNull(String token);

    List<RefreshToken> findAllByUserIdAndDeletedAtIsNull(Long userId);

    // 삭제 포함한 토큰 조회 (로그아웃 등)
    @Query("SELECT r FROM RefreshToken r WHERE r.token = :token")
    Optional<RefreshToken> findByTokenIncludingDeleted(@Param("token") String token);

    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId")
    List<RefreshToken> findAllByUserIdIncludingDeleted(@Param("userId") Long userId);
}