package xyz.tomorrowlearncamp.bookking.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UpdateUserRequest {

    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    private final String nickname;

    @NotBlank(message = "주소는 필수 입력 사항입니다.")
    private final String address;

    public UpdateUserRequest(String nickname, String address) {
        this.nickname = nickname;
        this.address = address;
    }
}
