package xyz.tomorrowlearncamp.bookking.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`user`")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
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


    public static User of(String email, String password, String name, UserRole roleUser, String address, Gender gender, int age, String nickname) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(roleUser)
                .address(address)
                .gender(gender)
                .age(age)
                .nickname(nickname)
                .build();
    }

    //TODO :
    public String getProfileImageUrl() {

        return null;
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

}
