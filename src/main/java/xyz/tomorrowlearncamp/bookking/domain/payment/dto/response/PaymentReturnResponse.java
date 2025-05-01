package xyz.tomorrowlearncamp.bookking.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PaymentReturnResponse {

    private final Long orderId;

    private final Long returnMoney;
}
