package xyz.tomorrowlearncamp.bookking.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.repository.OrderRepository;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponse::of);
    }

    @Transactional
    public Order createOrder(Long userId, Long bookId, String prePrice, Long stock, String publisher, String bookIntroductionUrl, OrderStatus status, PayType payType) {
        Order order = Order.builder()
                .userId(userId)
                .bookId(bookId)
                .prePrice(prePrice)
                .stock(stock)
                .publisher(publisher)
                .bookIntroductionUrl(bookIntroductionUrl)
                .status(status)
                .payType(payType)
                .build();
        return orderRepository.save(order);
    }

    // review 작성시 필요
    @Transactional(readOnly = true)
    public Long getPurchasedOrderId(Long userId, Long bookId) {
        return orderRepository.findCompletedOrder(userId, bookId, OrderStatus.COMPLETED)
                .map(Order::getOrderId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.PURCHASE_HISTORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order getOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.ORDER_NOT_FOUND)
        );
        return OrderResponse.of(getOrder);
    }


    @Transactional
    public void switchReviewStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORDER_NOT_FOUND));
        order.toggleReviewed();
    }
}
