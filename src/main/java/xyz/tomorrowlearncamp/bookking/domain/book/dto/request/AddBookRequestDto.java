package xyz.tomorrowlearncamp.bookking.domain.book.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddBookRequestDto {
	@NotNull
	private String isbn;
	@NotNull
	private String title;
	@NotNull
	private String subject;
	@NotNull
	private String author;
	@NotNull
	private String publisher;
	@NotNull
	private String bookIntroductionUrl;
	@NotNull
	private String prePrice;
	@NotNull
	private String page;
	@NotNull
	private String titleUrl;
	@NotNull
	private LocalDateTime publicationDate;
	@NotNull
	private Long stock;
}
