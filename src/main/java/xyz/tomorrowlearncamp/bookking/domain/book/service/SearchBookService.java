package xyz.tomorrowlearncamp.bookking.domain.book.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.esotericsoftware.minlog.Log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.SearchBookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.mapper.BookMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchBookService {
	private final WebClient webClient;
	private final BookMapper bookMapper;
	private final BookService bookService;

	@Value("${library.api.key}")
	private String apiKey;

	private static final String BASE_URL = "https://www.nl.go.kr/seoji/SearchApi.do?";

	public SearchBookResponseDto searchBooks(SearchBookRequestDto requestDto) {
		String url = buildUrl(requestDto);
		Log.info(url);
		return webClient.get()
			.uri(url)
			.retrieve()
			.bodyToMono(SearchBookResponseDto.class)
			.block();
	}

	public String buildUrl(SearchBookRequestDto requestDto) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL)
			.queryParam("cert_key", apiKey)
			.queryParam("result_style", "json")
			.queryParam("page_no", requestDto.getPageNo())
			.queryParam("page_size", requestDto.getPageSize());

		requestDto.toParamMap().forEach((key, value) -> {
			if (value != null && !value.isBlank()) {
				builder.queryParam(key, value);
			}
		});

		return builder.build().toUriString();
	}

	public void fetchBooksFromLibraryApi(int pageSize, int totalPage) {
		long start = System.currentTimeMillis();
		int bookCount = 0;
		for (int page = 1; page <= totalPage; page++) {
			log.info("Fetching page {}/{}", page, totalPage);
			SearchBookRequestDto requestDto = new SearchBookRequestDto();
			requestDto.setPageNo(page);
			requestDto.setPageSize(pageSize);

			SearchBookResponseDto responseDto = searchBooks(requestDto);
			List<BookDto> bookDtos = responseDto.getDocs();

			List<Book> books = bookDtos.stream()
				.map(bookMapper::toEntity)
				.collect(Collectors.toList());

			bookService.saveBooksInBatch(books, pageSize);

			bookCount += books.size();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		long end = System.currentTimeMillis();
		log.info("Inserted {} books! Duration of time: {} ms", bookCount,(end - start));
	}
}
