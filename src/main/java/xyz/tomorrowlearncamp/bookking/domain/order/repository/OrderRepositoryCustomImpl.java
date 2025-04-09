package xyz.tomorrowlearncamp.bookking.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

import static xyz.tomorrowlearncamp.bookking.domain.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        List<Order> content = queryFactory
                .selectFrom(order)
                .where(order.userId.eq(userId))
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(order.count())
                .from(order)
                .where(order.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0);
    }

    @Override
    public Optional<Order> findCompletedOrder(Long userId, Long bookId, OrderStatus status) {
        Order result = queryFactory
                .selectFrom(order)
                .where(
                        order.userId.eq(userId),
                        order.bookId.eq(bookId),
                        order.status.eq(status),
                        order.isReviewed.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
