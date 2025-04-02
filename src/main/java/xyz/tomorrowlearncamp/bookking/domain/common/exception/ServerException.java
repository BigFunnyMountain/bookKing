package xyz.tomorrowlearncamp.bookking.domain.common.exception;

public class ServerException extends RuntimeException{
	public ServerException(String message) {
		super(message);
	}
}
