package xyz.tomorrowlearncamp.bookking.domain.payment.service;

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
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.ServerException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.response.PaymentReturnResponse;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
	@Mock
	private BookRepository bookRepository;
	@Mock
	private RedissonClient redissonClient;
	@Mock
	private OrderService orderService;
	@Mock
	private RLock rlock;

	@InjectMocks
	private PaymentService paymentService;

	@Test
	@DisplayName("레디스 페어락 실패 테스트")
	void setFairLockFailedTest() throws InterruptedException {
		//given
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(false);

		// when && then
		ServerException assertThrows = assertThrows(ServerException.class,
				() -> paymentService.paymentV2(1L, 1L, 1L, 1L, PayType.KAKAO_PAY));

		assertInstanceOf(ServerException.class, assertThrows);
		assertEquals(ErrorMessage.REDIS_ERROR, assertThrows.getErrorMessage());
	}

	@Test
	@DisplayName("단독 구매 성공 테스트")
	void paymentSingleSuccessTest() throws InterruptedException {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 1L);
		ReflectionTestUtils.setField(book, "prePrice", "1");

		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		// when
		paymentService.paymentV2(user.getId(), 1L, 1L, 1L, PayType.KAKAO_PAY);

		// then
		verify(bookRepository, times(1)).save(any(book.getClass()));
		verify(orderService, times(1))
			.createOrder(1L, 1L, "1", 1L,null, null, OrderStatus.COMPLETED, PayType.KAKAO_PAY);
		assertEquals(0, book.getStock());
	}



	@Test
	@DisplayName("단독 구매 실패 테스트 : 존재 하지 않는 책")
	void paymentSingleFailedTest1() throws InterruptedException {
		//given
		given(bookRepository.findById(anyLong())).willReturn(Optional.empty());
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		// when && then
		NotFoundException assertThrows = assertThrows(NotFoundException.class,
				() -> paymentService.paymentV2(1L, 1L, 1L, 1L, PayType.KAKAO_PAY));

		assertInstanceOf(NotFoundException.class, assertThrows);
		assertEquals(ErrorMessage.BOOK_NOT_FOUND, assertThrows.getErrorMessage());
	}

	@Test
	@DisplayName("단독 구매 실패 테스트 : 책 권수 없음")
	void paymentSingleFailedTest2() throws InterruptedException {
		//given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 0L);
		ReflectionTestUtils.setField(book, "prePrice", "1");

		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		// when && then
		InvalidRequestException assertThrows = assertThrows(InvalidRequestException.class,
				() -> paymentService.paymentV2(1L, 1L, 1L, 1L, PayType.KAKAO_PAY));

		assertInstanceOf(InvalidRequestException.class, assertThrows);
		assertEquals(ErrorMessage.ZERO_BOOK_STOCK, assertThrows.getErrorMessage());
	}

	@Test
	@DisplayName("단독 구매 실패 테스트 : 사용자의 돈 없음")
	void paymentSingleFailedTest3() throws InterruptedException {
		//given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 1L);
		ReflectionTestUtils.setField(book, "prePrice", "1");

		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		// when && then
		InvalidRequestException assertThrows = assertThrows(InvalidRequestException.class,
				() -> paymentService.paymentV2(1L, 1L, 1L, 0L, PayType.KAKAO_PAY));

		assertInstanceOf(InvalidRequestException.class, assertThrows);
		assertEquals(ErrorMessage.SHORT_ON_MONEY, assertThrows.getErrorMessage());
	}

	@Test
	@DisplayName("멀티 쓰래드 v1 구매 성공 테스트")
	void paymentV1MutiSuccessTest() throws InterruptedException {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 100L);
		ReflectionTestUtils.setField(book, "prePrice", "1");

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		given(bookRepository.findByIdWithLock(anyLong())).willReturn(book);
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		//when
		for (int i = 0; i < threadCount; i++) {
			Long userId = (long)i;
			executorService.execute(() -> {
				try {
					paymentService.paymentV1(userId, 1L, 1L, 1L, PayType.KAKAO_PAY);
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



	@Test
	@DisplayName("멀티 쓰래드 v2 구매 성공 테스트")
	void paymentV2MutiSuccessTest() throws InterruptedException {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 100L);
		ReflectionTestUtils.setField(book, "prePrice", "1");

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		//when
		for (int i = 0; i < threadCount; i++) {
			Long userId = (long)i;
			executorService.execute(() -> {
				try {
					paymentService.paymentV2(userId, 1L, 1L, 1L, PayType.KAKAO_PAY);
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

	@Test
	@DisplayName("단독 환불 성공 테스트")
	void returnPaymentSingleSuccessTest() throws InterruptedException {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Order order = Order.builder()
				.userId(1L)
				.bookId(1L)
				.prePrice("15000")
				.stock(3L)
				.publisher("테스트 출판사")
				.bookIntroductionUrl("http://test-url.com")
				.status(OrderStatus.COMPLETED)
				.build();
		ReflectionTestUtils.setField(order, "orderId", 1L);

		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "stock", 97L);
		ReflectionTestUtils.setField(book, "prePrice", "1");

		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(orderService.getOrder(anyLong())).willReturn(OrderResponse.of(order));
		given(redissonClient.getFairLock(anyString())).willReturn(rlock);
		given(rlock.tryLock(100L, 10L, TimeUnit.SECONDS)).willReturn(true);

		// when
		PaymentReturnResponse returnResponse = paymentService.returnPayment(user.getId(), order.getOrderId());

		// then
		verify(bookRepository, times(1)).save(any(book.getClass()));
		assertEquals(45000, returnResponse.getReturnMoney());
		assertEquals(order.getOrderId(), returnResponse.getOrderId());
		assertEquals(100, book.getStock());
	}

	@Test
	@DisplayName("단독 환불 실패 테스트 : 오더의 유저와 같은 유저가 아님")
	void returnPaymentSingleFailedTest1() {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Order order = Order.builder()
				.userId(2L)
				.bookId(1L)
				.prePrice("15000")
				.stock(3L)
				.publisher("테스트 출판사")
				.bookIntroductionUrl("http://test-url.com")
				.status(OrderStatus.COMPLETED)
				.build();
		ReflectionTestUtils.setField(order, "orderId", 1L);

		given(orderService.getOrder(anyLong())).willReturn(OrderResponse.of(order));

		// when && then
		try {
			paymentService.returnPayment(user.getId(), order.getOrderId());
		} catch (InvalidRequestException e) {
			assertInstanceOf(InvalidRequestException.class, e);
			assertEquals(ErrorMessage.NO_AUTHORITY_TO_RETURN_A_PAYMENT, e.getErrorMessage());
			return;
		}
		fail();
	}

	@Test
	@DisplayName("단독 환불 실패 테스트 : 오더의 유저와 같은 유저가 아님")
	void returnPaymentSingleFailedTest2() {
		//given
		UserResponse user = new UserResponse();
		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "email", "test@test.com");
		ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

		Order order = Order.builder()
				.userId(2L)
				.bookId(1L)
				.prePrice("15000")
				.stock(3L)
				.publisher("테스트 출판사")
				.bookIntroductionUrl("http://test-url.com")
				.status(OrderStatus.COMPLETED)
				.build();
		ReflectionTestUtils.setField(order, "orderId", 1L);

		given(orderService.getOrder(anyLong())).willReturn(OrderResponse.of(order));

		// when && then
		try {
			paymentService.returnPayment(user.getId(), order.getOrderId());
		} catch (InvalidRequestException e) {
			assertInstanceOf(InvalidRequestException.class, e);
			assertEquals(ErrorMessage.NO_AUTHORITY_TO_RETURN_A_PAYMENT, e.getErrorMessage());
			return;
		}
		fail();
	}
}