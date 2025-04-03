package xyz.tomorrowlearncamp.bookking.order;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.request.OrderRequest;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.response.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.repository.OrderRepository;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.user.entity.User;
import xyz.tomorrowlearncamp.bookking.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        Long bookId = 100L;
        OrderRequest orderRequest = new OrderRequest();
        ReflectionTestUtils.setField(orderRequest, "bookId", bookId);
        ReflectionTestUtils.setField(orderRequest, "price", 500L);

        // 테스트용 User 생성
        User user = new User("test@test.com", "testpassword!", "test",
                null, "test1", null, 21, "testName");
        ReflectionTestUtils.setField(user, "id", userId);

        // 테스트용 Book 생성
        Book book = new Book();
        ReflectionTestUtils.setField(book, "bookId", bookId);
        ReflectionTestUtils.setField(book, "title", "테스트 도서");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        // todo BookService 구현 후 다시 확인
        // given(bookService.getBookById(bookId)).willReturn(book);

        // when
        orderService.createOrder(userId, orderRequest);

        // then
        verify(orderRepository).save(ArgumentMatchers.argThat(order ->
                order.getUser().equals(user) &&
                        order.getBook().equals(book) &&
                        order.getPrice().equals(500L) &&
                        order.getStatus() == OrderStatus.COMPLETED
        ));
    }

    @Test
    void 주문_생성_실패_사용자없음() {
        // given
        Long userId = 1L;
        Long bookId = 100L;
        OrderRequest orderRequest = new OrderRequest();
        ReflectionTestUtils.setField(orderRequest, "bookId", bookId);
        ReflectionTestUtils.setField(orderRequest, "price", 500L);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(userId, orderRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void 주문_조회_성공() {
        // given
        Long userId = 1L;
        Order order1 = createOrderForTest(userId, 1L, "테스트 도서1", 500L, OrderStatus.COMPLETED);
        Order order2 = createOrderForTest(userId, 2L, "테스트 도서2", 600L, OrderStatus.COMPLETED);
        List<Order> orders = List.of(order1, order2);
        Page<Order> page = new PageImpl<>(orders);

        given(orderRepository.findByUserId(ArgumentMatchers.eq(userId), ArgumentMatchers.any(Pageable.class)))
                .willReturn(page);

        // when
        Page<OrderResponse> responsePage = orderService.getMyOrders(userId, 0, 10);

        // then
        assertThat(responsePage.getContent()).hasSize(2);
        OrderResponse resp1 = responsePage.getContent().get(0);
        assertThat(resp1.getOrderId()).isEqualTo(order1.getOrderId());
        assertThat(resp1.getBookTitle()).isEqualTo("테스트 도서1");
        assertThat(resp1.getPrice()).isEqualTo(500L);
    }

    @Test
    void 환불_성공() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        Order order = createOrderForTest(userId, orderId, "테스트 도서", 500L, OrderStatus.COMPLETED);

        given(orderRepository.findByIdAndUserId(orderId, userId))
                .willReturn(Optional.of(order));

        // when
        orderService.refundOrder(userId, orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void 환불_실패_주문없음_또는_권한없음() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        given(orderRepository.findByIdAndUserId(orderId, userId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.refundOrder(userId, orderId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("주문이 존재하지 않거나 권한이 없습니다.");
    }

    @Test
    void 환불_실패_이미_취소된_주문() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        Order order = createOrderForTest(userId, orderId, "테스트 도서", 500L, OrderStatus.CANCELLED);

        given(orderRepository.findByIdAndUserId(orderId, userId))
                .willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.refundOrder(userId, orderId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이미 취소된 주문입니다.");
    }

    @Test
    void 사용자_구매_여부_확인_성공() {
        // given
        Long userId = 1L;
        Long bookId = 100L;
        given(orderRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, OrderStatus.COMPLETED))
                .willReturn(true);

        // when
        boolean result = orderService.hasUserPurchasedBook(userId, bookId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 사용자_구매_여부_확인_실패() {
        // given
        Long userId = 1L;
        Long bookId = 100L;
        given(orderRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, OrderStatus.COMPLETED))
                .willReturn(false);

        // when
        boolean result = orderService.hasUserPurchasedBook(userId, bookId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 주문_후기_작성_성공() {
        // given
        Long orderId = 1L;
        Order order = createOrderForTest(1L, orderId, "테스트 도서", 500L, OrderStatus.COMPLETED);

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        // when
        orderService.markAsReviewed(orderId);

        // then
        assertThat(order.isReviewed()).isTrue();
    }

    @Test
    void 주문_후기_작성_실패_주문없음() {
        // given
        Long orderId = 1L;
        given(orderRepository.findById(orderId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.markAsReviewed(orderId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("주문을 찾을 수 없습니다.");
    }

    // 테스트용 Order 객체 생성 헬퍼 메서드
    private Order createOrderForTest(Long userId, Long orderId, String bookTitle, Long price, OrderStatus status) {
        User user = new User("test@test.com", "testpassword!", "test",
                null, "test1", null, 21, "testName");
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = new Book();
        ReflectionTestUtils.setField(book, "bookId", orderId + 1000); // 임의의 값
        ReflectionTestUtils.setField(book, "title", bookTitle);

        Order order = Order.builder()
                .user(user)
                .book(book)
                .price(price)
                .status(status)
                .build();
        ReflectionTestUtils.setField(order, "orderId", orderId);
        return order;
    }
}
