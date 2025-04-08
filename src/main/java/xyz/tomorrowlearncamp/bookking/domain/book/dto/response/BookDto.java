package xyz.tomorrowlearncamp.bookking.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookDto {
	@JsonProperty("PUBLISHER")
	private String publisher;

	@JsonProperty("DDC")
	private String ddc;

	@JsonProperty("UPDATE_DATE")
	private String updateDate;

	@JsonProperty("EA_ADD_CODE")
	private String eaAddCode;

	@JsonProperty("PUBLISHER_URL")
	private String publisherUrl;

	@JsonProperty("AUTHOR")
	private String author;

	@JsonProperty("SERIES_TITLE")
	private String seriesTitle;

	@JsonProperty("KDC")
	private String kdc;

	@JsonProperty("EDITION_STMT")
	private String editionStmt;

	@JsonProperty("BOOK_TB_CNT_URL")
	private String bookTbCntUrl;

	@JsonProperty("BOOK_TB_CNT")
	private String bookTbCnt;

	@JsonProperty("BOOK_INTRODUCTION_URL")
	private String bookIntroductionUrl;

	@JsonProperty("BOOK_INTRODUCTION")
	private String bookIntroduction;

	@JsonProperty("BOOK_SUMMARY_URL")
	private String bookSummaryUrl;

	@JsonProperty("BOOK_SUMMARY")
	private String bookSummary;

	@JsonProperty("TITLE_URL")
	private String titleUrl;

	@JsonProperty("SET_ISBN")
	private String setIsbn;

	@JsonProperty("REAL_PUBLISH_DATE")
	private String realPublishDate;

	@JsonProperty("PRE_PRICE")
	private String prePrice;

	@JsonProperty("DEPOSIT_YN")
	private String depositYn;

	@JsonProperty("BOOK_SIZE")
	private String bookSize;

	@JsonProperty("EBOOK_YN")
	private String ebookYn;

	@JsonProperty("REAL_PRICE")
	private String realPrice;

	@JsonProperty("FORM")
	private String form;

	@JsonProperty("CONTROL_NO")
	private String controlNo;

	@JsonProperty("SERIES_NO")
	private String seriesNo;

	@JsonProperty("EA_ISBN")
	private String eaIsbn;

	@JsonProperty("INPUT_DATE")
	private String inputDate;

	@JsonProperty("SET_EXPRESSION")
	private String setExpression;

	@JsonProperty("VOL")
	private String vol;

	@JsonProperty("CIP_YN")
	private String cipYn;

	@JsonProperty("SUBJECT")
	private String subject;

	@JsonProperty("BIB_YN")
	private String bibYn;

	@JsonProperty("TITLE")
	private String title;

	@JsonProperty("PUBLISH_PREDATE")
	private String publishPredate;

	@JsonProperty("SET_ADD_CODE")
	private String setAddCode;

	@JsonProperty("PAGE")
	private String page;

	@JsonProperty("RELATED_ISBN")
	private String relatedIsbn;

	@JsonProperty("FORM_DETAIL")
	private String formDetail;
}

