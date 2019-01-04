package eu.printingin3d.smalogger.api.exception;

import java.io.IOException;

public class InvalidPasswordException extends IOException {
	private static final long serialVersionUID = 7594186677479303213L;

	public InvalidPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPasswordException(String message) {
		super(message);
	}

	public InvalidPasswordException(Throwable cause) {
		super(cause);
	}

}
