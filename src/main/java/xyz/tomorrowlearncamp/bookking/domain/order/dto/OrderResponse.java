package xyz.tomorrowlearncamp.bookking.domain.order.dto;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private Long orderId;
    private Long bookId;
    private Long userId;
    private String status;
    private boolean isReviewed;
    private LocalDateTime createdAt;
    private String prePrice;
    private Long buyStock;
    private String publisher;
    private String bookIntroductionUrl;

    public OrderResponse(Long orderId, Long bookId, Long userId, String status, boolean isReviewed, LocalDateTime createdAt,
                         String prePrice, Long stock, String publisher, String bookIntroductionUrl) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.userId = userId;
        this.status = status;
        this.isReviewed = isReviewed;
        this.createdAt = createdAt;
        this.prePrice = prePrice;
        this.buyStock = stock;
        this.publisher = publisher;
        this.bookIntroductionUrl = bookIntroductionUrl;
    }

    public static OrderResponse of(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getBookId(),
                order.getUserId(),
                order.getStatus().name(),
                order.isReviewed(),
                order.getCreatedAt(),
                order.getPrePrice(),
                order.getBuyStock(),
                order.getPublisher(),
                order.getBookIntroductionUrl()
        );
    }
}
