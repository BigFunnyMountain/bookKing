package xyz.tomorrowlearncamp.bookking.domain.book.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.SearchBookResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchBookService {
	private final WebClient webClient;

	@Value("${library.api.key}")
	private String apiKey;

	private static final String BASE_URL = "https://www.nl.go.kr/seoji/SearchApi.do?";

	public SearchBookResponseDto searchBooks(SearchBookRequestDto requestDto) {
		String url = buildUrl(requestDto);

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
}
