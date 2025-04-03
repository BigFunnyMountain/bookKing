package xyz.tomorrowlearncamp.bookking.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.request.OrderRequest;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.response.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.AuthUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody OrderRequest request
    ) {
        orderService.createOrder(authUser.getUserId(), request);
        return ResponseEntity.ok("주문이 성공적으로 생성되었습니다.");
    }

    @GetMapping("/me")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getMyOrders(authUser.getUserId(), page, size));
    }

    @PatchMapping("/{orderId}/refund")
    public ResponseEntity<String> refundOrder(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId
    ) {
        orderService.refundOrder(authUser.getUserId(), orderId);
        return ResponseEntity.ok("환불이 성공적으로 처리되었습니다.");
    }
}
