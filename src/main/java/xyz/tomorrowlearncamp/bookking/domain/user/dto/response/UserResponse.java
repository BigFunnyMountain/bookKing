package xyz.tomorrowlearncamp.bookking.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private UserRole role;
    private Gender gender;
    private String address;

    @Builder
    private UserResponse(Long id, String email, String nickname, String profileImageUrl, UserRole role, Gender gender, String address) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.gender = gender;
        this.address = address;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .gender(user.getGender())
                .address(user.getAddress())
                .build();
    }
}
