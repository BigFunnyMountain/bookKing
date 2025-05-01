package xyz.tomorrowlearncamp.bookking.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentReturnRequest {

    @NotNull
    @Positive
    private final Long orderId;

    @NotNull
    @Positive
    private final Long bookId;

}
