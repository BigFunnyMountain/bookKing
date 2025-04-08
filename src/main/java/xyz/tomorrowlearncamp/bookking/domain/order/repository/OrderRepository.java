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

    // 사용자의 구매 내역 조회 (최신순)
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // 사용자가 특정 책을 주문한 적 있는지 (리뷰 작성 전 검증)
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.userId = :userId AND o.bookId = :bookId AND o.status = :status")
    boolean existsByUserIdAndBookIdAndStatus(@Param("userId") Long userId,
                                             @Param("bookId") Long bookId,
                                             @Param("status") OrderStatus status);

    // 리뷰 작성 전 주문 완료 체크
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.bookId = :bookId AND o.status = :status AND o.isReviewed = false")
    Optional<Order> findCompletedOrderByUserAndBook(@Param("userId") Long userId,
                                                    @Param("bookId") Long bookId,
                                                    @Param("status") OrderStatus status);

}
