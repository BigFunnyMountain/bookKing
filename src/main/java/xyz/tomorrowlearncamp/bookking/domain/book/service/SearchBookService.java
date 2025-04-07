package xyz.tomorrowlearncamp.bookking.domain.book.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequestDto;

@Service
@RequiredArgsConstructor
public class SearchBookService {
	private final WebClient webClient;

	@Value("${library.api.key}")
	private String apiKey;

	private static final String BASE_URL = "https://www.nl.go.kr/seoji/SearchApi.do?";

	public String searchBooks(SearchBookRequestDto requestDto){
		String url = buildUrl(requestDto);
		System.out.println(url);

		return webClient.get()
			.uri(url)
			.retrieve()
			.toString();
	}

	public String buildUrl(SearchBookRequestDto requestDto){
		StringBuilder url = new StringBuilder(BASE_URL);
		url.append("?key=").append(apiKey);
		url.append("&result_style=json");
		url.append("&page_no=").append(requestDto.getPageNo());
		url.append("&page_size=").append(requestDto.getPageSize());

		if (requestDto.getIsbn() != null) {
			url.append("&isbn=").append(encode(requestDto.getIsbn()));
		}
		if (requestDto.getSetIsbn() != null) {
			url.append("&set_isbn=").append(encode(requestDto.getSetIsbn()));
		}
		if (requestDto.getEbookYn() != null) {
			url.append("&ebook_yn=").append(requestDto.getEbookYn());
		}
		if (requestDto.getTitle() != null) {
			url.append("&title=").append(encode(requestDto.getTitle()));
		}
		if (requestDto.getStartPublishDate() != null) {
			url.append("&start_publish_date=").append(requestDto.getStartPublishDate());
		}
		if (requestDto.getEndPublishDate() != null) {
			url.append("&end_publish_date=").append(requestDto.getEndPublishDate());
		}
		if (requestDto.getCipYn() != null) {
			url.append("&cip_yn=").append(requestDto.getCipYn());
		}
		if (requestDto.getDepositYn() != null) {
			url.append("&deposit_yn=").append(requestDto.getDepositYn());
		}
		if (requestDto.getSeriesTitle() != null) {
			url.append("&series_title=").append(encode(requestDto.getSeriesTitle()));
		}
		if (requestDto.getPublisher() != null) {
			url.append("&publisher=").append(encode(requestDto.getPublisher()));
		}
		if (requestDto.getAuthor() != null) {
			url.append("&author=").append(encode(requestDto.getAuthor()));
		}
		if (requestDto.getForm() != null) {
			url.append("&form=").append(encode(requestDto.getForm()));
		}
		if (requestDto.getSort() != null) {
			url.append("&sort=").append(requestDto.getSort());
		}
		if (requestDto.getOrderBy() != null) {
			url.append("&order_by=").append(requestDto.getOrderBy());
		}

		return url.toString();
	}

	private String encode(String param) {
		return URLEncoder.encode(param, StandardCharsets.UTF_8);
	}
}
