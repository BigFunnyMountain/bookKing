package xyz.tomorrowlearncamp.bookking.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자의 주문 목록 조회 (최신순)
    @Query("SELECT o FROM Order o JOIN FETCH o.book WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // 사용자가 특정 책을 주문한 적 있는지 (리뷰 작성 전 검증)
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.user.id = :userId AND o.book.bookId = :bookId AND o.status = :status")
    boolean existsByUserIdAndBookIdAndStatus(@Param("userId") Long userId,
                                             @Param("bookId") Long bookId,
                                             @Param("status") OrderStatus status);

    // 주문이 해당 유저의 것인지 체크 (환불 등 검증)
    @Query("SELECT o FROM Order o JOIN FETCH o.book WHERE o.orderId = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserId(@Param("orderId") Long orderId,
                                      @Param("userId") Long userId);
}
