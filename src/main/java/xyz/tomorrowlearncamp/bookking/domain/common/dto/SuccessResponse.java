package xyz.tomorrowlearncamp.bookking.domain.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SuccessResponse<T> implements Response<T> {

	private final T data;

	@Override
	public T getError() {
		return null;
	}
}
