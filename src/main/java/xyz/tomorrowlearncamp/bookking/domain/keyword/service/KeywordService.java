package xyz.tomorrowlearncamp.bookking.domain.keyword.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.keyword.dto.KeywordResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.dto.OrderResponse;
import xyz.tomorrowlearncamp.bookking.domain.order.service.OrderService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final OpenAiService openAiService;
    private final OrderService orderService;
    private final BookRepository bookRepository;

    // 검색어 기반 키워드 추천
    public KeywordResponse suggestKeywords(String searchTerm) {
        String prompt = String.format(
            "Suggest 5 related search keywords for '%s'. " +
            "Return only the keywords separated by commas, no additional text.",
            searchTerm
        );

        return getKeywordsFromOpenAI(prompt);
    }

    // 사용자 구매 이력 기반 개인화된 키워드 추천
    @Transactional(readOnly = true)
    public KeywordResponse suggestByOrder(Long userId) {
        List<OrderResponse> recentOrders = orderService.getMyOrders(userId, 0, 10).getContent();

        List<Book> purchasedBooks = recentOrders.stream()
                .map(order -> bookRepository.findById(order.getBookId())
                        .orElseThrow(() -> new RuntimeException("Book not found")))
                .collect(Collectors.toList());

        // 책들의 정보를 기반으로 프롬프트 생성
        String bookInfo = purchasedBooks.stream()
                .map(book -> String.format("Title: %s, Publisher: %s", book.getTitle(), book.getPublisher()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                "Based on the user's recent book purchases:\n%s\n\n" +
                        "Suggest 5 related book categories or topics that the user might be interested in. " +
                        "Respond in Korean. Return only the keywords separated by commas, no additional text.",
                bookInfo
        );

        return getKeywordsFromOpenAI(prompt);
    }

     // OpenAI API를 사용하여 키워드 추천
    private KeywordResponse getKeywordsFromOpenAI(String prompt) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(prompt)
                .maxTokens(100)
                .temperature(0.7)
                .build();

        String response = openAiService.createCompletion(completionRequest)
                .getChoices()
                .get(0)
                .getText()
                .trim();

        List<String> keywords = Arrays.stream(response.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        KeywordResponse keywordResponse = KeywordResponse.of(keywords);
        return keywordResponse;
    }
} 