package xyz.tomorrowlearncamp.bookking.domain.book.dto.response;

import lombok.Getter;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

@Getter
public class BookResponse {
	private final Long bookId;
	private final String title;
	private final String subject;
	private final String author;
	private final String publisher;
	private final String bookIntroductionUrl;
	private final String prePrice;
	private final String publicationDate;
	private final Long stock;

	public BookResponse(Book book) {
		this.bookId = book.getBookId();
		this.title = book.getTitle();
		this.subject = book.getSubject();
		this.author = book.getAuthor();
		this.publisher = book.getPublisher();
		this.bookIntroductionUrl = book.getBookIntroductionUrl();
		this.prePrice = book.getPrePrice();
		this.publicationDate = book.getPublicationDate();
		this.stock = book.getStock();
	}

	public static BookResponse of(Book book) {
		return new BookResponse(book);
	}
}
