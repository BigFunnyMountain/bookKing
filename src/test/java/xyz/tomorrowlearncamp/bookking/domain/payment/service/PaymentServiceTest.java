package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private PaymentService paymentService;

	@Mock
	private RedissonClient redissonClient;

	@Mock
	private OrderService orderService;

	@Mock
	private RLock rlock;

	@Test
	@DisplayName("단일 호출 테스트")
	void paymentTest() throws InterruptedException {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 1L);
		ReflectionTestUtils.setField(book, "prePrice", 1L);

		given(userService.getMyInfo(anyLong())).willReturn(user);
		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		// when
		paymentService.payment(user.getId(), 1L, 1L, 1L, PayType.KAKAO_PAY);

		// then
		verify(bookRepository, times(1)).save(any(book.getClass()));
		verify(orderService, times(1))
			.createOrder(1L, 1L, 1L, 0L, null, null, OrderStatus.COMPLETED);
		assertEquals(0, book.getStock());
	}


	@Test
	@DisplayName("멀티 쓰래드 테스트")
	void paymentTest_thread() throws InterruptedException {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 100L);
		ReflectionTestUtils.setField(book, "prePrice", 1L);

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		given(userService.getMyInfo(anyLong())).willReturn(user);
		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		//when
		for (int i = 0; i < threadCount; i++) {
			Long userId = (long)i;
			executorService.execute(() -> {
				try {
					paymentService.payment(userId, 1L, 1L, 1L, PayType.KAKAO_PAY);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		// then
		verify(bookRepository, times(100)).save(any(book.getClass()));
		assertEquals(0, book.getStock());
	}
}