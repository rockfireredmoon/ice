package org.icescripting;

public class ScriptEvalException extends RuntimeException {

	public ScriptEvalException() {
		super();
	}

	public ScriptEvalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScriptEvalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptEvalException(String message) {
		super(message);
	}

	public ScriptEvalException(Throwable cause) {
		super(cause);
	}

}
