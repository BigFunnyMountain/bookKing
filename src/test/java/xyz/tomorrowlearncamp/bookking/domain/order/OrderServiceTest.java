package xyz.tomorrowlearncamp.bookking.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.repository.OrderRepository;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문_목록_조회_성공() {
        // given
        Long userId = 1L;
        int page = 0;
        int size = 2;

        Book book = Book.builder()
                .bookId(1L)
                .title("Effective Java")
                .stock(10L)
                .build();

        User user = User.builder()
                .nickname("nickname")
                .gender(Gender.MALE)
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Order order1 = Order.builder()
                .userId(user.getId())
                .bookId(book.getBookId())
                .prePrice("10000")
                .stock(book.getStock())
                .publisher("Some Publisher")
                .bookIntroductionUrl("http://example.com/book")
                .status(OrderStatus.COMPLETED)
                .build();
        ReflectionTestUtils.setField(order1, "orderId", 1L);
        ReflectionTestUtils.setField(order1, "createdAt", LocalDateTime.now().minusDays(1));

        Order order2 = Order.builder()
                .userId(user.getId())
                .bookId(book.getBookId())
                .prePrice("10000")
                .stock(book.getStock())
                .publisher("Some Publisher")
                .bookIntroductionUrl("http://example.com/book")
                .status(OrderStatus.COMPLETED)
                .build();
        ReflectionTestUtils.setField(order2, "orderId", 2L);
        ReflectionTestUtils.setField(order2, "createdAt", LocalDateTime.now());

        List<Order> orders = List.of(order2, order1);
        Page<Order> orderPage = new PageImpl<>(orders);

        given(orderRepository.findByUserId(eq(userId), any(Pageable.class)))
                .willReturn(orderPage);

        // when
        Page<OrderResponse> result = orderService.getMyOrders(userId, page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().get(0).getOrderId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getOrderId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getBookId()).isEqualTo(1L);
        // 추가된 필드에 대한 검증 (필요한 경우)
        assertThat(result.getContent().get(0).getPrePrice()).isEqualTo("10000");
        assertThat(result.getContent().get(0).getPublisher()).isEqualTo("Some Publisher");

        verify(orderRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void 주문_목록_조회_빈결과_테스트() {
        // given
        Long userId = 1L;
        int page = 0;
        int size = 2;

        Page<Order> emptyPage = Page.empty();
        given(orderRepository.findByUserId(eq(userId), any(Pageable.class)))
                .willReturn(emptyPage);

        // when
        Page<OrderResponse> result = orderService.getMyOrders(userId, page, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(orderRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        Long bookId = 2L;
        String prePrice = "15000";
        Long stock = 5L;
        String publisher = "테스트 출판사";
        String bookIntroductionUrl = "http://test-url.com";
        OrderStatus status = OrderStatus.COMPLETED;
        PayType payType = PayType.CARD;

        Order savedOrder = Order.builder()
                .userId(userId)
                .bookId(bookId)
                .prePrice(prePrice)
                .stock(stock)
                .publisher(publisher)
                .bookIntroductionUrl(bookIntroductionUrl)
                .status(status)
                .build();

        ReflectionTestUtils.setField(savedOrder, "orderId", 100L);
        ReflectionTestUtils.setField(savedOrder, "createdAt", LocalDateTime.now());

        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        // when
        Order result = orderService.createOrder(userId, bookId, prePrice, publisher, bookIntroductionUrl, status, payType);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(100L);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBookId()).isEqualTo(bookId);
        assertThat(result.getPrePrice()).isEqualTo(prePrice);
        assertThat(result.getPublisher()).isEqualTo(publisher);
        assertThat(result.getBookIntroductionUrl()).isEqualTo(bookIntroductionUrl);
        assertThat(result.getStatus()).isEqualTo(status);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void 사용자_구매_내역_존재시_orderId_반환() {
        // given
        Long userId = 1L;
        Long bookId = 2L;
        Long expectedOrderId = 100L;

        Order order = Order.builder()
                .userId(userId)
                .bookId(bookId)
                .prePrice("15000")
                .stock(3L)
                .publisher("테스트 출판사")
                .bookIntroductionUrl("http://test-url.com")
                .status(OrderStatus.COMPLETED)
                .build();

        ReflectionTestUtils.setField(order, "orderId", expectedOrderId);

        given(orderRepository.findCompletedOrder(userId, bookId, OrderStatus.COMPLETED))
                .willReturn(Optional.of(order));

        // when
        Long result = orderService.getPurchasedOrderId(userId, bookId);

        // then
        assertThat(result).isEqualTo(expectedOrderId);
        verify(orderRepository).findCompletedOrder(userId, bookId, OrderStatus.COMPLETED);
    }


    @Test
    void 사용자_구매_내역_없을때_예외발생() {
        // given
        Long userId = 1L;
        Long bookId = 2L;

        given(orderRepository.findCompletedOrder(userId, bookId, OrderStatus.COMPLETED))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getPurchasedOrderId(userId, bookId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("구매 이력이 존재하지 않습니다.");

        verify(orderRepository).findCompletedOrder(userId, bookId, OrderStatus.COMPLETED);
    }


    @Test
    void 리뷰_상태_토글_성공() {
        // given
        Long orderId = 1L;

        Order order = Order.builder()
                .userId(1L)
                .bookId(2L)
                .prePrice("15000")
                .stock(3L)
                .publisher("테스트 출판사")
                .bookIntroductionUrl("http://test-url.com")
                .status(OrderStatus.COMPLETED)
                .build();

        ReflectionTestUtils.setField(order, "orderId", orderId);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        orderService.switchReviewStatus(orderId);

        // then
        assertThat(order.isReviewed()).isTrue(); // 토글 한 번 -> true
        verify(orderRepository).findById(orderId);
    }

    @Test
    void 리뷰_상태_토글_실패_주문없음() {
        // given
        Long orderId = 999L;

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.switchReviewStatus(orderId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("주문을 찾을 수 없습니다.");

        verify(orderRepository).findById(orderId);
    }
}
