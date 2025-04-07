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
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final UserService userService;

	private final BookRepository bookRepository;

	// private final OrderService orderService;
	
	private final RedissonClient redissonClient;

	public void payment(/*Long userId, */Long bookId, Long buyStock, Long money, PayType payType) {
		// UserResponse user = userService.getMyInfo(userId);


		RLock lock = redissonClient.getFairLock("book:"+bookId);

		try {
			boolean acquired = lock.tryLock(100L, 10L, TimeUnit.SECONDS);
			if (!acquired) {
				throw new InterruptedException();
			}
			Book book = bookRepository.findById(bookId).orElseThrow(()->new NotFoundException("Book not found"));

			// 책이 재고가 0개인 경우 || 구매하려는 개수 만큼 없는 경우
			if( book.getStock() == 0 || book.getStock() < buyStock ) {
				throw new InvalidRequestException(ErrorMessage.ZERO_BOOK_STOCK.getMessage());
			}

			// 돈이 부족한 경우
			if( money < buyStock * book.getPrePrice() ) {
				throw new InvalidRequestException(ErrorMessage.SHORT_ON_MONEY.getMessage());
			}

			// buyStock 만큼 구매
			book.updateStock(book.getStock() - buyStock);

			bookRepository.save(book);

			// todo : 오더 로직

		} catch (InterruptedException ex) {
			throw new InvalidRequestException(ErrorMessage.REDIS_ERROR.getMessage());
		} catch ( InvalidRequestException ex ) {
			throw new InvalidRequestException(ex.getMessage());
		} catch (Exception ex) {
			throw new InvalidRequestException(ErrorMessage.ERROR.getMessage());
		} finally {
			lock.unlock();
		}
	}
}

