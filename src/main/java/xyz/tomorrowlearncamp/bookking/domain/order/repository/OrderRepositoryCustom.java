package xyz.tomorrowlearncamp.bookking.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Page<Order> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Optional<Order> findCompletedOrder(Long userId, Long bookId, OrderStatus status);

    Optional<Order> findByIdAndDeletedAtIsNull(Long orderId);
}