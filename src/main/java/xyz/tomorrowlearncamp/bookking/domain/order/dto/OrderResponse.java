package xyz.tomorrowlearncamp.bookking.domain.order.dto;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private final Long orderId;
    private final Long bookId;
    private final Long userId;
    private final String status;
    private final boolean isReviewed;
    private final LocalDateTime createdAt;
    private final String prePrice;
    private final Long buyStock;
    private final String publisher;
    private final String bookIntroductionUrl;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.bookId = order.getBookId();
        this.userId = order.getUserId();
        this.status = order.getStatus().name();
        this.isReviewed = order.isReviewed();
        this.createdAt = order.getCreatedAt();
        this.prePrice = order.getPrePrice();
        this.buyStock = order.getBuyStock();
        this.publisher = order.getPublisher();
        this.bookIntroductionUrl = order.getBookIntroductionUrl();
    }

    public static OrderResponse of(Order order) {
        return new OrderResponse(order);
    }
}
