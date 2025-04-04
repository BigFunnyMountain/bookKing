package xyz.tomorrowlearncamp.bookking.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.request.OrderRequest;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.response.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.repository.OrderRepository;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.user.entity.User;
import xyz.tomorrowlearncamp.bookking.user.enums.Gender;
import xyz.tomorrowlearncamp.bookking.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static xyz.tomorrowlearncamp.bookking.user.enums.UserRole.ROLE_USER;

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

    /*
        createOrder 테스트
     */
    @Test
    void 주문_생성_성공_재고감소_확인() {
        // given
        Long userId = 1L;
        Long bookId = 1L;
        Long price = 10000L;

        User user = User.builder()
                .email("email1@email.com")
                .password("password1")
                .name("Test")
                .role(ROLE_USER)
                .address("address1")
                .gender(Gender.MALE)
                .age(20)
                .nickname("Test")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = Book.builder()
                .bookId(bookId)
                .isbn("isbn")
                .title("title")
                .subject("subject")
                .author("author")
                .publisher("publisher")
                .bookIntroductionUrl("bookIntroductionUrl")
                .prePrice(price)
                .page(300L)
                .titleUrl("titleUrl")
                .publicationDate(LocalDateTime.of(2024, 4, 1, 0, 0))
                .stock(2L)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "bookId", bookId);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        // when
        orderService.createOrder(userId, request);

        // then
        verify(orderRepository).save(captor.capture());
        Order savedOrder = captor.getValue();

        assertThat(savedOrder.getUser()).isEqualTo(user);
        assertThat(savedOrder.getBook()).isEqualTo(book);
        assertThat(savedOrder.getPrice()).isEqualTo(price);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(savedOrder.isReviewed()).isFalse();

        // 재고 감소 확인
        assertThat(book.getStock()).isEqualTo(1L);
    }

    @Test
    void 주문_생성_실패_사용자_없음() {
        // given
        Long userId = 1L;
        Long bookId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "bookId", bookId);

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void 주문_생성_실패_책_없음() {
        // given
        Long userId = 1L;
        Long bookId = 1L;

        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .name("Test")
                .role(ROLE_USER)
                .address("addr")
                .gender(Gender.MALE)
                .age(25)
                .nickname("nick")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "bookId", bookId);

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    void 주문_생성_실패_재고_없음() {
        // given
        Long userId = 1L;
        Long bookId = 1L;
        Long price = 15000L;

        User user = User.builder()
                .email("test@email.com")
                .password("pass")
                .name("Test")
                .role(ROLE_USER)
                .address("somewhere")
                .gender(Gender.FEMALE)
                .age(30)
                .nickname("tester")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Book book = Book.builder()
                .bookId(bookId)
                .isbn("isbn")
                .title("title")
                .subject("subject")
                .author("author")
                .publisher("publisher")
                .bookIntroductionUrl("url")
                .prePrice(price)
                .page(100L)
                .titleUrl("turl")
                .publicationDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .stock(0L) // 재고 없음
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "bookId", bookId);

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(userId, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("재고가 부족합니다.");
    }

    /*
        getMyOrders 테스트
     */
    @Test
    void 주문_목록_조회_성공() {
        // given
        Long userId = 1L;
        int page = 0;
        int size = 2;

        Book book = Book.builder()
                .bookId(1L)
                .isbn("isbn")
                .title("Effective Java")
                .subject("Programming")
                .author("Joshua Bloch")
                .publisher("Addison-Wesley")
                .bookIntroductionUrl("introUrl")
                .prePrice(30000L)
                .page(400L)
                .titleUrl("titleUrl")
                .publicationDate(LocalDateTime.of(2023, 1, 1, 0, 0))
                .stock(10L)
                .build();

        User user = User.builder()
                .email("user@email.com")
                .password("pass")
                .name("User")
                .role(ROLE_USER)
                .address("Seoul")
                .gender(Gender.MALE)
                .age(25)
                .nickname("nickname")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Order order1 = Order.builder()
                .user(user)
                .book(book)
                .price(30000L)
                .status(OrderStatus.COMPLETED)
                .build();
        Order order2 = Order.builder()
                .user(user)
                .book(book)
                .price(30000L)
                .status(OrderStatus.COMPLETED)
                .build();

        ReflectionTestUtils.setField(order1, "orderId", 1L);
        ReflectionTestUtils.setField(order2, "orderId", 2L);
        ReflectionTestUtils.setField(order1, "createdAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(order2, "createdAt", LocalDateTime.now());

        List<Order> orders = List.of(order2, order1); // 최신 순
        Page<Order> orderPage = new PageImpl<>(orders);

        given(orderRepository.findByUserId(eq(userId), any(Pageable.class)))
                .willReturn(orderPage);

        // when
        Page<OrderResponse> result = orderService.getMyOrders(userId, page, size);

        // then
        assertThat(result.getContent().get(0).getOrderId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getOrderId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getBookTitle()).isEqualTo("Effective Java");

        verify(orderRepository).findByUserId(eq(userId), any(Pageable.class));
    }

    /*
        refundOrder 테스트
     */
    @Test
    void 주문_환불_성공() {
        // given
        Long userId = 1L;
        Long orderId = 1L;

        Book book = Book.builder()
                .bookId(1L)
                .isbn("isbn")
                .title("title")
                .subject("subject")
                .author("author")
                .publisher("publisher")
                .bookIntroductionUrl("url")
                .prePrice(10000L)
                .page(100L)
                .titleUrl("titleUrl")
                .publicationDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .stock(1L)
                .build();

        User user = User.builder()
                .email("user@email.com")
                .password("pass")
                .name("User")
                .role(ROLE_USER)
                .address("Seoul")
                .gender(Gender.MALE)
                .age(25)
                .nickname("nickname")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Order order = Order.builder()
                .user(user)
                .book(book)
                .price(10000L)
                .status(OrderStatus.COMPLETED)
                .build();
        ReflectionTestUtils.setField(order, "orderId", orderId);

        given(orderRepository.findByIdAndUserId(orderId, userId))
                .willReturn(Optional.of(order));

        // when
        orderService.refundOrder(userId, orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(book.getStock()).isEqualTo(2L); // 원래 1L 였으니 +1
        verify(orderRepository).findByIdAndUserId(orderId, userId);
    }

    @Test
    void 주문_환불_실패_이미_취소된_주문() {
        // given
        Long userId = 1L;
        Long orderId = 1L;

        Book book = Book.builder()
                .bookId(1L)
                .isbn("isbn")
                .title("title")
                .subject("subject")
                .author("author")
                .publisher("publisher")
                .bookIntroductionUrl("url")
                .prePrice(10000L)
                .page(100L)
                .titleUrl("titleUrl")
                .publicationDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .stock(1L)
                .build();

        User user = User.builder()
                .email("user@email.com")
                .password("pass")
                .name("User")
                .role(ROLE_USER)
                .address("Seoul")
                .gender(Gender.MALE)
                .age(25)
                .nickname("nickname")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Order order = Order.builder()
                .user(user)
                .book(book)
                .price(10000L)
                .status(OrderStatus.CANCELLED) // 이미 취소된 상태
                .build();
        ReflectionTestUtils.setField(order, "orderId", orderId);

        given(orderRepository.findByIdAndUserId(orderId, userId))
                .willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.refundOrder(userId, orderId))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("이미 취소된 주문입니다.");
    }
}
