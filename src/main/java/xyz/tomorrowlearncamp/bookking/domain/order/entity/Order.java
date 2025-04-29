package xyz.tomorrowlearncamp.bookking.domain.order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private String prePrice;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String bookIntroductionUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private boolean isReviewed;

    @Column(nullable = false)
    private PayType payType;

    @Builder
    public Order(Long userId, Long bookId, String prePrice, Long stock, String publisher, String bookIntroductionUrl, OrderStatus status, PayType payType) {
        this.userId = userId;
        this.bookId = bookId;
        this.prePrice = prePrice;
        this.stock = stock;
        this.publisher = publisher;
        this.bookIntroductionUrl = bookIntroductionUrl;
        this.status = status;
        this.payType = payType;
        this.isReviewed = false;
    }

    public void toggleReviewed() {
        this.isReviewed = !this.isReviewed;
    }
}
