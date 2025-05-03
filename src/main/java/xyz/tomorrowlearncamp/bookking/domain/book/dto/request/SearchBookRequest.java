package xyz.tomorrowlearncamp.bookking.domain.book.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchBookRequest {
	@NotNull
	private int pageNo = 1;
	@NotNull
	private int pageSize = 10;
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

	public Map<String, String> toParamMap() {
		Map<String, String> paramMap = new LinkedHashMap<>();
		paramMap.put("isbn", isbn);
		paramMap.put("set_isbn", setIsbn);
		paramMap.put("ebook_yn", ebookYn);
		paramMap.put("title", title);
		paramMap.put("start_publish_date", startPublishDate);
		paramMap.put("end_publish_date", endPublishDate);
		paramMap.put("cip_yn", cipYn);
		paramMap.put("deposit_yn", depositYn);
		paramMap.put("series_title", seriesTitle);
		paramMap.put("publisher", publisher);
		paramMap.put("author", author);
		paramMap.put("form", form);
		paramMap.put("sort", sort);
		paramMap.put("order_by", orderBy);
		return paramMap;
	}
}
