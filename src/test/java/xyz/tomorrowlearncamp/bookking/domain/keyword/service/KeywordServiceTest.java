package xyz.tomorrowlearncamp.bookking.domain.keyword.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.keyword.dto.KeywordResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.entity.Order;
import xyz.tomorrowlearncamp.bookking.domain.order.enums.OrderStatus;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @Mock
    private OpenAiService openAiService;

    @Mock
    private OrderService orderService;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private KeywordService keywordService;

    @Test
    @DisplayName("검색어 기반 키워드 추천 성공")
    void suggestKeywords_success() {
        // given
        String searchTerm = "자바";
        // 예상 응답 5개
        String expectedResponse = "Java 프로그래밍, 스프링 프레임워크, JVM, 객체지향 프로그래밍, 자바 개발";
        
        CompletionChoice choice = new CompletionChoice();
        ReflectionTestUtils.setField(choice, "text", expectedResponse);
        
        CompletionResult completionResult = new CompletionResult();
        ReflectionTestUtils.setField(completionResult, "choices", List.of(choice));

        given(openAiService.createCompletion(any(CompletionRequest.class)))
                .willReturn(completionResult);

        // when
        KeywordResponse response = keywordService.suggestKeywords(searchTerm);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSuggestedKeywords()).hasSize(5);
        // 특정 키워드가 포함되어있는지 확인(정확하게 나왔는지 확인하는 것)
        assertThat(response.getSuggestedKeywords()).contains("Java 프로그래밍", "스프링 프레임워크");
        verify(openAiService).createCompletion(any(CompletionRequest.class));
    }

    @Test
    @DisplayName("구매 이력 기반 키워드 추천 성공")
    void suggestKeywordsByPurchaseHistory_success() {
        // given
        Long userId = 1L;
        Long bookId = 1L;

        Book book = Book.builder()
                .bookId(bookId)
                .title("Effective Java")
                .publisher("Addison-Wesley")
                .build();

        Order order = Order.builder()
                .userId(userId)
                .bookId(bookId)
                .status(OrderStatus.COMPLETED)
                .build();
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now());

        Page<OrderResponse> orderPage = new PageImpl<>(List.of(OrderResponse.of(order)));
        given(orderService.getMyOrders(userId, 0, 10)).willReturn(orderPage);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        String expectedResponse = "Java 프로그래밍, 객체지향 설계, 소프트웨어 개발, 프로그래밍 패턴, 코드 품질";
        
        CompletionChoice choice = new CompletionChoice();
        ReflectionTestUtils.setField(choice, "text", expectedResponse);
        
        CompletionResult completionResult = new CompletionResult();
        ReflectionTestUtils.setField(completionResult, "choices", List.of(choice));

        given(openAiService.createCompletion(any(CompletionRequest.class)))
                .willReturn(completionResult);

        // when
        KeywordResponse response = keywordService.suggestByOrder(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSuggestedKeywords()).hasSize(5);
        assertThat(response.getSuggestedKeywords()).contains("Java 프로그래밍", "객체지향 설계");
        verify(orderService).getMyOrders(userId, 0, 10);
        verify(bookRepository).findById(bookId);
        verify(openAiService).createCompletion(any(CompletionRequest.class));
    }

    @Test
    @DisplayName("구매 이력 기반 키워드 추천 실패 - 책을 찾을 수 없음")
    void suggestKeywordsByPurchaseHistory_fail_bookNotFound() {
        // given
        Long userId = 1L;
        Long bookId = 1L;

        Order order = Order.builder()
                .userId(userId)
                .bookId(bookId)
                .status(OrderStatus.COMPLETED)
                .build();
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.now());

        Page<OrderResponse> orderPage = new PageImpl<>(List.of(OrderResponse.of(order)));
        given(orderService.getMyOrders(userId, 0, 10)).willReturn(orderPage);
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> keywordService.suggestByOrder(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");

        verify(orderService).getMyOrders(userId, 0, 10);
        verify(bookRepository).findById(bookId);
    }
} 