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
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.repository.OrderRepository;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.user.entity.User;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
                .prePrice(10000L)
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
                .prePrice(10000L)
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
        assertThat(result.getContent().get(0).getPrePrice()).isEqualTo(10000L);
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
}
