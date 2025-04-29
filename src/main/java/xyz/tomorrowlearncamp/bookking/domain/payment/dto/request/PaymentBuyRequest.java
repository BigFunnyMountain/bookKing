package xyz.tomorrowlearncamp.bookking.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;

@Getter
@RequiredArgsConstructor
public class PaymentBuyRequest {

	@NotNull
	@Positive
	private final Long bookId;

	@NotNull
	@Positive
	private final Long money;

	@NotNull
	private final PayType payType;

	// 구매하는 갯수
	@NotNull
	@Positive
	private final Long buyStock;
}
