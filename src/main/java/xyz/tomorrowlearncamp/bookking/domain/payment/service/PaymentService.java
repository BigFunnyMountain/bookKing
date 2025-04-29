package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.ServerException;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.response.PaymentReturnResponse;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final UserService userService;

	private final BookRepository bookRepository;

	private final OrderService orderService;
	
	private final RedissonClient redissonClient;

	private RLock lock;

	public void payment(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {
		Book book = new Book();
		setFairLock(bookId);
		try {
			book = bookRepository.findById(bookId).orElseThrow(
					() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND)
			);
			// 책이 재고가 0개인 경우 || 구매하려는 개수 만큼 없는 경우
			if (book.getStock() == 0 || book.getStock() < buyStock) {
				throw new InvalidRequestException(ErrorMessage.ZERO_BOOK_STOCK);
			}

			// 돈이 부족한 경우
			long price = Long.parseLong(book.getPrePrice());
			if (price * buyStock > money) {
				throw new InvalidRequestException(ErrorMessage.SHORT_ON_MONEY);
			}

			// buyStock 만큼 구매
			book.updateStock(book.getStock() - buyStock);
			bookRepository.save(book);
		} catch (NumberFormatException ex) {
			throw new InvalidRequestException(ErrorMessage.CALL_ADMIN);
		} catch (Exception ex) {
			throw new ServerException(ErrorMessage.ERROR);
		} finally {
			lock.unlock();
		}
		orderService.createOrder(userId, book.getBookId(), book.getPrePrice(), book.getPublisher(), book.getBookIntroductionUrl(), OrderStatus.COMPLETED, payType);
	}

	public PaymentReturnResponse returnPayment(Long userId, Long orderId) {
		OrderResponse order = orderService.getOrderEntity(orderId);
		if(!ObjectUtils.nullSafeEquals(userId, order.getUserId())) {
			throw new InvalidRequestException(ErrorMessage.NO_AUTHORITY_TO_RETURN_A_PAYMENT);
		}

		setFairLock(order.getBookId());

		try {
			Book book = bookRepository.findById(order.getBookId()).orElseThrow(
					() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND)
			);
			book.updateStock(book.getStock() + order.getStock());
			bookRepository.save(book);
		} catch (Exception ex) {
			throw new ServerException(ErrorMessage.ERROR);
		} finally {
			lock.unlock();
		}

		Long price = Long.parseLong(order.getPrePrice());
		return PaymentReturnResponse.builder()
				.orderId(orderId)
				.returnMoney(order.getStock() * price)
				.build();
	}

	private void setFairLock(Long bookId) {
		lock = redissonClient.getFairLock("book:"+bookId);
		boolean acquired = false;
		try {
			acquired = lock.tryLock(100L, 10L, TimeUnit.SECONDS);
			if (!acquired) {
				throw new ServerException(ErrorMessage.REDIS_ERROR);
			}
		} catch (ServerException | InterruptedException ex) {
			log.warn("redis Interrupted!");
			Thread.currentThread().interrupt();
			throw new ServerException(ErrorMessage.REDIS_ERROR);
		}
	}
}

