package xyz.tomorrowlearncamp.bookking.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.request.OrderRequest;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.response.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.repository.OrderRepository;
import xyz.tomorrowlearncamp.bookking.user.entity.User;
import xyz.tomorrowlearncamp.bookking.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("책을 찾을 수 없습니다."));

        if (book.getStock() <= 0) {
            throw new InvalidRequestException("재고가 부족합니다.");
        }

        book.updateStock(book.getStock() - 1);

        Order order = Order.builder()
                .user(user)
                .book(book)
                .price(book.getPrePrice())
                .status(OrderStatus.COMPLETED)
                .build();

        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponse::from);
    }

    @Transactional
    public void refundOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("주문이 존재하지 않거나 권한이 없습니다."));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidRequestException("이미 취소된 주문입니다.");
        }

        Book book = order.getBook();
        book.updateStock(book.getStock() + 1);

        if (order.isReviewed()) {
            order.unmarkAsReviewed();
        }

        order.cancel();
    }

    @Transactional(readOnly = true)
    public boolean hasUserPurchasedBook(Long userId, Long bookId) {
        return orderRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, OrderStatus.COMPLETED);
    }

    @Transactional
    public void markAsReviewed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        order.markAsReviewed();
    }

    @Transactional(readOnly = true)
    public Order findCompletedOrder(Long userId, Long bookId) {
        return orderRepository.findCompletedOrderByUserAndBook(userId, bookId, OrderStatus.COMPLETED)
                .orElseThrow(() -> new NotFoundException("해당 책에 대한 완료된 주문이 없습니다."));
    }

    // OrderService.java
    @Transactional
    public void unmarkAsReviewed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        order.unmarkAsReviewed();
    }

}
