package xyz.tomorrowlearncamp.bookking.domain.payment.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.bookking.common.exception.NotFoundException;
import xyz.tomorrowlearncamp.bookking.common.exception.ServerException;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;
import xyz.tomorrowlearncamp.bookking.domain.payment.dto.response.PaymentReturnResponse;
import xyz.tomorrowlearncamp.bookking.domain.payment.enums.PayType;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

  private final RedissonClient redissonClient;
  private final PaymentService paymentService;
  private final OrderService orderService;

  public void paymentV2(Long userId, Long bookId, Long buyStock, Long money, PayType payType) {

    RLock lock = redissonClient.getFairLock("book:" + bookId);
    boolean acquired = false;

    try {
      acquired = lock.tryLock(10L, 100L, TimeUnit.MILLISECONDS);
      if (!acquired) {
        throw new InvalidRequestException(ErrorMessage.REDIS_ERROR);
      }

      paymentService.paymentV2(userId, bookId, buyStock, money, payType);


    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new ServerException(ErrorMessage.REDIS_ERROR);
    } catch (NotFoundException ex) {
      throw new NotFoundException(ex.getErrorMessage());
    } catch (InvalidRequestException ex) {
      throw new InvalidRequestException(ex.getErrorMessage());
    } catch (Exception ex) {
      throw new ServerException(ErrorMessage.ERROR);
    } finally {
      if (acquired && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  public PaymentReturnResponse returnPayment(Long userId, Long orderId) {
    OrderResponse order = orderService.getOrder(orderId);

    RLock lock = redissonClient.getFairLock("book:" + order.getBookId());
    boolean acquired = false;

    try {
      acquired = lock.tryLock(10L, 100L, TimeUnit.MILLISECONDS);
      if (!acquired) {
        throw new InvalidRequestException(ErrorMessage.REDIS_ERROR);
      }

      return paymentService.returnPayment(userId, order);


    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new ServerException(ErrorMessage.REDIS_ERROR);
    } catch (NotFoundException ex) {
      throw new NotFoundException(ex.getErrorMessage());
    } catch (InvalidRequestException ex) {
      throw new InvalidRequestException(ex.getErrorMessage());
    } catch (Exception ex) {
      throw new ServerException(ErrorMessage.ERROR);
    } finally {
      if (acquired && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }
}