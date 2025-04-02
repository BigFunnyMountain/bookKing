package xyz.tomorrowlearncamp.bookking.domain.common.exception;


public class InvalidRequestException extends RuntimeException{
	public InvalidRequestException(String message) {
		super(message);
	}
}
