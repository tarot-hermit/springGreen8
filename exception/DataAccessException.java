package exception;

@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

	// 메시지만 받을 때
	public DataAccessException(String message) {super(message);}

	// 메시지 + 원인(cause)을 함께 받을 때
	public DataAccessException(String message, Throwable cause) {super(message, cause);}
}