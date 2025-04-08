package xyz.tomorrowlearncamp.bookking.domain.book.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchBookResponseDto {
	@JsonProperty("TOTAL_COUNT")
	private String totalCount;

	@JsonProperty("PAGE_NO")
	private String pageNo;

	@JsonProperty("docs")
	private List<BookDto> docs;
}
