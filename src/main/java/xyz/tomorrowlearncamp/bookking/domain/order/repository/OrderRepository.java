package xyz.tomorrowlearncamp.bookking.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
//    // 복잡 쿼리가 아니라 단순 쿼리라서 custom말고 해당 인터페이스에 추가
//    // 삭제되지 않은 주문만 조회
//    Optional<Order> findByIdAndDeletedAtIsNull(Long id);
//
//    // 삭제 여부 상관없이 주문 조회
//    @Query("SELECT o FROM Order o WHERE o.id = :id")
//    Optional<Order> findByIdIncludingDeleted(@Param("id") Long id);
}
