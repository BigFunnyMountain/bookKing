package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.common.enums.LogType;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.common.exception.ServerException;
import xyz.tomorrowlearncamp.bookking.common.util.LogUtil;

import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.response.PaymentReturnResponse;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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

	@Transactional
	public void paymentV1(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {
		if(userService.existsById(userId)) {
			throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		Book book = bookRepository.findByIdWithLock(bookId);

		checkPayment(book, buyStock, money);
		book.updateStock(book.getStock() - buyStock);
		orderService.createOrder(userId, book, buyStock, OrderStatus.COMPLETED, payType);
	}

	public void paymentV2(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {
		if(!userService.existsById(userId)) {
			throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		Book book = new Book();

		try {
			setFairLock(bookId);
			book = bookRepository.findById(bookId).orElseThrow(
				() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND)
			);

			checkPayment(book, buyStock, money);
			book.updateStock(book.getStock() - buyStock);
			bookRepository.save(book);
		} catch (NotFoundException ex) {
			throw new NotFoundException(ex.getErrorMessage());
		} catch (NumberFormatException ex) {
			throw new InvalidRequestException(ErrorMessage.CALL_ADMIN);
		} catch (InvalidRequestException ex) {
			throw new InvalidRequestException(ex.getErrorMessage());
		} catch (Exception ex) {
			throw new ServerException(ErrorMessage.ERROR);
		} finally {
			lock.unlock();
		}
		orderService.createOrder(userId, book, buyStock, OrderStatus.COMPLETED, payType);

		UserResponse user = userService.getMyInfo(userId);

		Map<String, Object> log = new HashMap<>();
		log.put("log_type", "buy");
		log.put("age_group", LogUtil.getAgeGroup(user.getAge()));
		log.put("gender", user.getGender());
		log.put("price", book.getPrePrice());
		log.put("book_name", book.getTitle());
		log.put("timestamp", Instant.now().toString());

		LogUtil.log(LogType.PURCHASE, log);
	}

	public PaymentReturnResponse returnPayment(Long userId, Long orderId) {
		if(!userService.existsById(userId)) {
			throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		OrderResponse order = orderService.getOrder(orderId);
		if(!ObjectUtils.nullSafeEquals(userId, order.getUserId())) {
			throw new InvalidRequestException(ErrorMessage.NO_AUTHORITY_TO_RETURN_A_PAYMENT);
		}

		try {
			setFairLock(order.getBookId());
			Book book = bookRepository.findById(order.getBookId()).orElseThrow(
					() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND)
			);
			book.updateStock(book.getStock() + order.getBuyStock());
			bookRepository.save(book);
		} catch (NotFoundException ex) {
			throw new NotFoundException(ex.getErrorMessage());
		} catch (Exception ex) {
			throw new ServerException(ErrorMessage.ERROR);
		} finally {
			lock.unlock();
		}
		orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED);

		Long price = Long.parseLong(order.getPrePrice());
		return PaymentReturnResponse.builder()
				.orderId(orderId)
				.returnMoney(order.getBuyStock() * price)
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

	private void checkPayment(Book book, Long buyStock, Long money) {
		// 책이 재고가 0개인 경우 || 구매하려는 개수 만큼 없는 경우
		if (book.getStock() == 0 || book.getStock() < buyStock) {
			throw new InvalidRequestException(ErrorMessage.ZERO_BOOK_STOCK);
		}

		// 돈이 부족한 경우
		long price = Long.parseLong(book.getPrePrice());
		if (price * buyStock > money) {
			throw new InvalidRequestException(ErrorMessage.SHORT_ON_MONEY);
		}
	}
}

