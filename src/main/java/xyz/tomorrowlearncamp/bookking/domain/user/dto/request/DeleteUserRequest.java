package xyz.tomorrowlearncamp.bookking.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteUserRequest {

    @NotBlank(message = "비밀번호는 필수로 입력해주세요.")
    private String password;

    public DeleteUserRequest(String password) {
        this.password = password;
    }
}
