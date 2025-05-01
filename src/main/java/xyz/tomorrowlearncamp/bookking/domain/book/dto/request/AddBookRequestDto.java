package xyz.tomorrowlearncamp.bookking.domain.book.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.BookSource;

@Getter
public class AddBookRequestDto {
	private String title;
	private String subject;
	private String author;
	private String publisher;
	private String bookIntroductionUrl;
	private String prePrice;
	private String publicationDate;
	private final Long stock = 0L;
	@NotNull
	private BookSource source;
}
