package xyz.tomorrowlearncamp.bookking.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;

@Getter
@NoArgsConstructor
public class PaymentRequest {

	@NotNull
	@Positive
	private Long bookId;

	@NotNull
	@Positive
	private Long money;

	@NotNull
	private PayType payType;

	// 구매하는 갯수
	@NotNull
	@Positive
	private Long buyStock;

	public PaymentRequest(Long bookId, Long buyStock, Long money, PayType payType) {
		this.bookId = bookId;
		this.buyStock = buyStock;
		this.money = money;
		this.payType = payType;
	}
}
