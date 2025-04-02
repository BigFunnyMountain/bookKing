package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.PaymentRequest;

@Service
@RequiredArgsConstructor
public class PaymentService {

	// private final UserService userService;

	// private final BookService bookService;

	// private final OrderService orderService;
	
	private final RedissonClient redissonClient;

	@Transactional
	public void payment(/*Long userId, */Long bookId) {
		RLock lock = redissonClient.getFairLock("book");

		try {
			lock.lock();

		} catch (Exception e) {
			throw new InvalidRequestException(ErrorMessage.REDIS_ERROR.getErrorMessage());
		} finally {
			lock.unlock();
		}
	}
}

