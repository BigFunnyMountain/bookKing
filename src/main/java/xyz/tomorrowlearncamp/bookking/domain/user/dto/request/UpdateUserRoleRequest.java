package xyz.tomorrowlearncamp.bookking.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserRoleRequest {
    @NotBlank(message = "역할은 필수 입력 값입니다.")
    private String role;

    public UpdateUserRoleRequest(String role) {
        this.role = role;
    }
}
