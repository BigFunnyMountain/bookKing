package xyz.tomorrowlearncamp.bookking.domain.book.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

@Getter
public class BookResponseDto {
	private final Long bookId;
	private final String isbn;
	private final String title;
	private final String subject;
	private final String author;
	private final String publisher;
	private final String bookIntroductionUrl;
	private final String prePrice;
	private final String page;
	private final String titleUrl;
	private final String publicationDate;
	private final Long stock;

	public BookResponseDto(Book book) {
		this.bookId = book.getBookId();
		this.isbn = book.getIsbn();
		this.title = book.getTitle();
		this.subject = book.getSubject();
		this.author = book.getAuthor();
		this.publisher = book.getPublisher();
		this.bookIntroductionUrl = book.getBookIntroductionUrl();
		this.prePrice = book.getPrePrice();
		this.page = book.getPage();
		this.titleUrl = book.getTitleUrl();
		this.publicationDate = book.getPublicationDate();
		this.stock = book.getStock();
	}

	public static BookResponseDto of(Book book) {
		return new BookResponseDto(book);
	}
}
