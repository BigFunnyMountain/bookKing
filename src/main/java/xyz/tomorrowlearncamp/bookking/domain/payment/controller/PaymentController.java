package xyz.tomorrowlearncamp.bookking.domain.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.request.PaymentRequest;
import xyz.tomorrowlearncamp.bookking.domain.payment.service.PaymentService;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/v1/payments")
	public ResponseEntity<Void> payment(
		// @AuthenticationPrincipal AuthUser user,
		@Valid @RequestBody PaymentRequest paymentRequest
	) {
		paymentService.payment(
			// user.getUserId(),
			paymentRequest.getBookId(),
			paymentRequest.getBuyStock(),
			paymentRequest.getMoney(),
			paymentRequest.getPayType()
		);
		return ResponseEntity.ok().build();
	}
}
