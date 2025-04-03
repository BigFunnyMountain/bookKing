package xyz.tomorrowlearncamp.bookking.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderRequest {

    @NotNull(message = "책 ID는 필수입니다.")
    private Long bookId;

    @NotNull(message = "가격은 필수입니다.")
    private Long price;
}
