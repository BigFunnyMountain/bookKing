package xyz.tomorrowlearncamp.bookking.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.request.PaymentBuyRequest;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.response.PaymentReturnResponse;
import xyz.tomorrowlearncamp.bookking.domain.payment.service.PaymentService;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/v1/payments")
	public void paymentV1(
			@AuthenticationPrincipal AuthUser user,
			@Valid @RequestBody PaymentBuyRequest paymentBuyRequest
	) {
		paymentService.paymentV1(
			user.getUserId(),
			paymentBuyRequest.getBookId(),
			paymentBuyRequest.getBuyStock(),
			paymentBuyRequest.getMoney(),
			paymentBuyRequest.getPayType()
		);
	}

	@PostMapping("/v2/payments")
	public void paymentV2(
			@AuthenticationPrincipal AuthUser user,
			@Valid @RequestBody PaymentBuyRequest paymentBuyRequest
	) {
		paymentService.paymentV2(
			user.getUserId(),
			paymentBuyRequest.getBookId(),
			paymentBuyRequest.getBuyStock(),
			paymentBuyRequest.getMoney(),
			paymentBuyRequest.getPayType()
		);
	}

	@PostMapping("/v1/payment/{orderId}")
	public Response<PaymentReturnResponse> returnPayment(
			@AuthenticationPrincipal AuthUser user,
			@PathVariable Long orderId
	) {
		return Response.success(paymentService.returnPayment(user.getUserId(), orderId));
	}
}
