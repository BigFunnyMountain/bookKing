package xyz.tomorrowlearncamp.bookking.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

	@NotNull
	@Positive
	private Long bookId;

}
