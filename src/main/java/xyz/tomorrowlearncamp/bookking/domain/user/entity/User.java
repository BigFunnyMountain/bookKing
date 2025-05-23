package xyz.tomorrowlearncamp.bookking.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

import java.time.LocalDateTime;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private User(String email, String password, String name, UserRole role, String address, Gender gender, int age, String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.address = address;
        this.gender = gender;
        this.age = age;
        this.nickname = nickname;
    }


    public static User of(SignupRequest request, String encodedPassword, UserRole role) {
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .role(role)
                .address(request.getAddress())
                .gender(request.getGender())
                .age(request.getAge())
                .nickname(request.getNickname())
                .build();
    }

    public void updateProfileImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void updateUserInfo(String nickname, String address) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
        if (address != null && !address.isEmpty()) {
            this.address = address;
        }
    }

    public void updateRole(UserRole newRole) {
        this.role = newRole;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
