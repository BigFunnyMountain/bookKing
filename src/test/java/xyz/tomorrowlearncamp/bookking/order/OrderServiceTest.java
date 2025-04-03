package xyz.tomorrowlearncamp.bookking.order;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
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

@ExtendWith(MockitoExtension.class)  // (1) 스프링 컨텍스트 로딩 X, 순수 Mockito 테스트
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        Long bookId = 100L;

        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "bookId", bookId);
        ReflectionTestUtils.setField(request, "price", 500L);

        // 가짜 User, Book 엔티티 생성
        User user = createMockUser(userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

//        Book book = createMockBook(bookId, "테스트 도서");
//        given(bookService.getBookById(bookId)).willReturn(book);
        // todo Book구현 되면 지울 것
        Book book = new Book();
        // 필요한 필드를 ReflectionTestUtils 또는 setter로 주입합니다.
        ReflectionTestUtils.setField(book, "bookId", bookId);
        ReflectionTestUtils.setField(book, "title", "테스트 도서");

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));


        // when
        orderService.createOrder(userId, request);

        // then
        // 저장되는 Order 엔티티 검증
        verify(orderRepository).save(argThat(order ->
                order.getUser().equals(user) &&
                        order.getBook().equals(book) &&
                        order.getPrice().equals(500L) &&
                        order.getStatus() == OrderStatus.COMPLETED
        ));
    }

    // ======= 헬퍼 메서드 =======
    private User createMockUser(Long userId) {
        User user = new User("test@test.com", "testpassword!", "test",
                null, "test1", null, 21, "testName");
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private Book createMockBook(Long bookId, String title) {
        Book book = new Book();
        ReflectionTestUtils.setField(book, "bookId", bookId);
        ReflectionTestUtils.setField(book, "title", title);
        return book;
    }
}
