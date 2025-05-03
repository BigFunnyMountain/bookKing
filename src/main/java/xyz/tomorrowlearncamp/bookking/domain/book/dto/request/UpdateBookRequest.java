package xyz.tomorrowlearncamp.bookking.domain.book.dto.request;

import lombok.Getter;

@Getter
public class UpdateBookRequest {
	private String title;
	private String subject;
	private String author;
	private String publisher;
	private String bookIntroductionUrl;
	private String prePrice;
	private String publicationDate;
}
