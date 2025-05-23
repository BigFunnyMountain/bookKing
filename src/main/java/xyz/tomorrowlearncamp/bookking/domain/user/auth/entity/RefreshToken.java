package xyz.tomorrowlearncamp.bookking.domain.user.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.common.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }


    @Builder
    public RefreshToken(Long userId, String token, LocalDateTime expiredAt) {
        this.userId = userId;
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public static RefreshToken of(Long userId, String token, LocalDateTime expiredAt) {
        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiredAt(expiredAt)
                .build();
    }
}
