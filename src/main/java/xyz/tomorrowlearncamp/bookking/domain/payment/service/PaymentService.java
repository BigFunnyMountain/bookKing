package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	// private final UserService userService;

	// private final BookService bookService;
	private final BookRepository bookRepository;

	// private final OrderService orderService;
	
	private final RedissonClient redissonClient;

	// @Transactional
	public void payment(/*Long userId, */Long bookId) {
		Book book;
		RLock lock = redissonClient.getFairLock("book:"+bookId);

		try {
			boolean acquired = lock.tryLock(10L, 1L, TimeUnit.SECONDS);
			if (!acquired) {
				throw new InterruptedException();
			}
			book = bookRepository.findById(bookId).orElseThrow(
				() -> new NotFoundException(ErrorMessage.NOT_FOUND_BOOK.getMessage())
			);
			// log.info("" +book.getCount());
			book.CountMinusOne();
			log.info("" +book.getCount());
			//오더 로직 들어갈 예정
		} catch (InterruptedException ex) {
			throw new InvalidRequestException(ErrorMessage.REDIS_ERROR.getMessage());
		} catch (Exception ex) {
			throw new InvalidRequestException(ErrorMessage.ERROR.getMessage());
		} finally {
			if( lock.isLocked() )
			lock.unlock();
		}
	}
}

