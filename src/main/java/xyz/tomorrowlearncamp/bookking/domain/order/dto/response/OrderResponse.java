package xyz.tomorrowlearncamp.bookking.domain.order.dto.response;

import lombok.Builder;
import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private String status;
    private boolean isReviewed;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .bookId(order.getBook().getBookId())
                .bookTitle(order.getBook().getTitle())
                .userId(order.getUser().getId())
                .userName(order.getUser().getNickname())
                .status(order.getStatus().name())
                .isReviewed(order.isReviewed())
                .createdAt(order.getCreatedAt())
                .build();
    }
}