package xyz.tomorrowlearncamp.bookking.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.InvalidRequestException;

/**
 * 작성자 : 문성준 일시 : 2025.04.03 - v1
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
	ROLE_USER,
	ROLE_ADMIN;

	public static UserRole of(String input) {
		for (UserRole role : values()) {
			if (role.name().equalsIgnoreCase(input)) {
				return role;
			}
		}
		throw new InvalidRequestException(ErrorMessage.INVALID_USER_ROLE);
	}
}
