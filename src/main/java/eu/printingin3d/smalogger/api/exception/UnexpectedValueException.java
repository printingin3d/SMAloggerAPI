package eu.printingin3d.smalogger.api.exception;

import java.io.IOException;

public class UnexpectedValueException extends IOException {
	private static final long serialVersionUID = 2892599075306960299L;

	public UnexpectedValueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnexpectedValueException(String arg0) {
		super(arg0);
	}

	public UnexpectedValueException(Throwable arg0) {
		super(arg0);
	}
}
