package eu.printingin3d.smalogger.api.exception;

import java.io.IOException;

public class NoDataReceivedException extends IOException {
	private static final long serialVersionUID = -8695107806829334911L;

	public NoDataReceivedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoDataReceivedException(String message) {
		super(message);
	}

	public NoDataReceivedException(Throwable cause) {
		super(cause);
	}
}
