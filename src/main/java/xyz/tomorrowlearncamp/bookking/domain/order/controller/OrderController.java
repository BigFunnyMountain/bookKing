package xyz.tomorrowlearncamp.bookking.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/v1/orders/myinfo")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getMyOrders(authUser.getUserId(), page, size));
    }
}
