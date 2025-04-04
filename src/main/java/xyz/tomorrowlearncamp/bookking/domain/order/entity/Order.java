package xyz.tomorrowlearncamp.bookking.domain.order.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private boolean isReviewed;

    @Builder
    public Order(User user, Book book, Long price, OrderStatus status) {
        this.user = user;
        this.book = book;
        this.price = price;
        this.status = status;
        this.isReviewed = false;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void markAsReviewed() {
        this.isReviewed = true;
    }

    public void unmarkAsReviewed() {
        this.isReviewed = false;
    }

}
