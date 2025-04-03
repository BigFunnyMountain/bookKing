package xyz.tomorrowlearncamp.bookking.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String bookTitle;
    private Long price;
    private OrderStatus status;
    private boolean isReviewed;
    private LocalDateTime orderedAt;

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getBook().getTitle(),
                order.getPrice(),
                order.getStatus(),
                order.isReviewed(),
                order.getCreatedAt()
        );
    }
}
