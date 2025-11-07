package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.enums.LogType;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.common.exception.ServerException;
import xyz.tomorrowlearncamp.bookking.common.util.LogUtil;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.response.PaymentReturnResponse;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;
import xyz.tomorrowlearncamp.bookking.domain.user.dto.response.UserResponse;
import xyz.tomorrowlearncamp.bookking.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final UserService userService;
	private final BookRepository bookRepository;
	private final OrderService orderService;
	private final RedissonClient redissonClient;

	@Transactional
	public void paymentV1(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {
		if (!userService.existsById(userId)) {
			throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
		}
		Book book = bookRepository.findByIdWithLock(bookId);

		checkPayment(book, buyStock, money);
		book.updateStock(book.getStock() - buyStock);
		orderService.createOrder(userId, book, buyStock, OrderStatus.COMPLETED, payType);

		UserResponse user = userService.getMyInfo(userId);

		collectAndSendLog(user, book);
	}

	@Transactional
	public void paymentV2(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {
		UserResponse user = userService.getMyInfo(userId);
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND));

		checkPayment(book, buyStock, money);
		book.updateStock(book.getStock() - buyStock);
		bookRepository.save(book);

		orderService.createOrder(userId, book, buyStock, OrderStatus.COMPLETED, payType);
		collectAndSendLog(user, book);
	}

	@Transactional
	public PaymentReturnResponse returnPayment(Long userId, OrderResponse order) {
		if(!userService.existsById(userId)) {
			throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
		}

		if(!ObjectUtils.nullSafeEquals(userId, order.getUserId())) {
			throw new InvalidRequestException(ErrorMessage.NO_AUTHORITY_TO_RETURN_A_PAYMENT);
		}


		Book book = bookRepository.findById(order.getBookId()).orElseThrow(
				() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND)
		);
		book.updateStock(book.getStock() + order.getBuyStock());
		bookRepository.save(book);

		orderService.updateOrderStatus(order.getOrderId(), OrderStatus.REFUNDED);

		Long price = Long.parseLong(order.getPrePrice());
		return PaymentReturnResponse.builder()
				.orderId(order.getOrderId())
				.returnMoney(order.getBuyStock() * price)
				.build();
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

	private void collectAndSendLog(UserResponse user, Book book) {
		Map<String, Object> log = new HashMap<>();
		log.put("log_type", "buy");
		log.put("age_group", LogUtil.getAgeGroup(user.getAge()));
		log.put("gender", user.getGender());
		log.put("price", Integer.parseInt(book.getPrePrice()));
		log.put("book_name", book.getTitle());
		log.put("timestamp", Instant.now().toString());

		LogUtil.log(LogType.PURCHASE, log);
	}
}

