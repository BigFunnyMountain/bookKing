package xyz.tomorrowlearncamp.bookking.domain.book.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SearchBookRequestDto {
	@NotNull
	private int pageNo = 1;  // 기본값 1
	@NotNull
	private int pageSize = 10; // 기본값 10
	private String isbn;
	private String setIsbn;
	private String ebookYn;
	private String title;
	private String startPublishDate;
	private String endPublishDate;
	private String cipYn;
	private String depositYn;
	private String seriesTitle;
	private String publisher;
	private String author;
	private String form;
	private String sort;
	private String orderBy;
}
