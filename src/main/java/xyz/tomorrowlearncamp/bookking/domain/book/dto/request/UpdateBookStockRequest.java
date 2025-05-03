package xyz.tomorrowlearncamp.bookking.domain.book.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateBookStockRequest {
	@NotNull
	private Long stock;
}
