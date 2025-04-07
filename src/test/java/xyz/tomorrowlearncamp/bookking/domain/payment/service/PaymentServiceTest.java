package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private PaymentService paymentService;

	@Mock
	private RedissonClient redissonClient;

	@Mock
	private RLock rlock;

	@Test
	@DisplayName("단일 호출 테스트")
	void paymentTest() throws InterruptedException {
		//given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "count", 1000L);

		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(redissonClient.getFairLock("book:"+book.getBookId())).willReturn(rlock);
		given(rlock.tryLock(10L, 1L, TimeUnit.SECONDS)).willReturn(true);


		// when
		paymentService.payment(1L);

		// then
		verify(bookRepository).findById(1L);
		assertEquals(999, book.getCount());
	}


	@Test
	@DisplayName("멀티 쓰래드 테스트")
	void paymentTest_thread() throws InterruptedException {
		//given
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);
		ReflectionTestUtils.setField(book, "count", 1000L);

		int threadCount = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		given(redissonClient.getFairLock("book:"+book.getBookId())).willReturn(rlock);
		given(bookRepository.findById(anyLong())).willReturn(Optional.of(book));
		given(rlock.tryLock(10L, 1L, TimeUnit.SECONDS)).willReturn(true);


		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					paymentService.payment(1L);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		assertEquals(0, book.getCount());
	}
}