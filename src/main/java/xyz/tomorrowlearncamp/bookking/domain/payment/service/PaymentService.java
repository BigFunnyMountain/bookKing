package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final UserService userService;

	private final BookRepository bookRepository;

	private final OrderService orderService;
	
	private final RedissonClient redissonClient;

	public void payment(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {
		RLock lock = redissonClient.getFairLock("book:"+bookId);

		try {
			boolean acquired = lock.tryLock(100L, 10L, TimeUnit.SECONDS);
			if (!acquired) {
				throw new InterruptedException();
			}
			Book book = bookRepository.findById(bookId).orElseThrow(
				() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND)
			);
			// 책이 재고가 0개인 경우 || 구매하려는 개수 만큼 없는 경우
			if( book.getStock() == 0 || book.getStock() < buyStock ) {
				throw new InvalidRequestException(ErrorMessage.ZERO_BOOK_STOCK);
			}

			// 돈이 부족한 경우
			long price = Long.parseLong(book.getPrePrice());
			if( price * buyStock > money ) {
				throw new InvalidRequestException(ErrorMessage.SHORT_ON_MONEY);
			}

			// buyStock 만큼 구매
			book.updateStock(book.getStock() - buyStock);
			bookRepository.save(book);

			orderService.createOrder(userId, book.getBookId(), book.getPrePrice(), book.getStock(), book.getPublisher(), book.getBookIntroductionUrl(), OrderStatus.COMPLETED);

		} catch (InterruptedException ex) {
			throw new InvalidRequestException(ErrorMessage.REDIS_ERROR);
		} catch (NumberFormatException ex) {
			throw new InvalidRequestException(ErrorMessage.CALL_ADMIN);
		} catch (InvalidRequestException ex) {
			throw new InvalidRequestException(ex.getErrorMessage());
		} catch (Exception ex) {
			throw new InvalidRequestException(ErrorMessage.ERROR);
		} finally {
			lock.unlock();
		}
	}
}

