package xyz.tomorrowlearncamp.bookking.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class BookDto {
	@JsonProperty("PUBLISHER")
	private String publisher;

	@JsonProperty("AUTHOR")
	private String author;

	@JsonProperty("BOOK_INTRODUCTION_URL")
	private String bookIntroductionUrl;

	@JsonProperty("PRE_PRICE")
	private String prePrice;

	@JsonProperty("SUBJECT")
	private String subject;

	@JsonProperty("TITLE")
	private String title;

	@JsonProperty("PUBLISH_PREDATE")
	private String publishPredate;
}

