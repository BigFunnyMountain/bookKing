package xyz.tomorrowlearncamp.bookking.domain.user.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    @NotBlank
    @Email
    private String email;

    @Setter
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private Gender gender;

    @Min(1)
    @Max(120)
    private int age;

    @NotBlank
    private String nickname;

    @Builder
    private SignupRequest(String email, String password, String name, String address, Gender gender, int age, String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.age = age;
        this.nickname = nickname;
    }

    public static SignupRequest of(String email, String password, String name, String address, Gender gender, int age, String nickname) {
        return SignupRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .address(address)
                .gender(gender)
                .age(age)
                .nickname(nickname)
                .build();
    }
}
