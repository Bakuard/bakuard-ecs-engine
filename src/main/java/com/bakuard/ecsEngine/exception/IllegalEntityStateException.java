package com.bakuard.ecsEngine.exception;

public class IllegalEntityStateException extends RuntimeException {
	public IllegalEntityStateException() {
	}

	public IllegalEntityStateException(String message) {
		super(message);
	}

	public IllegalEntityStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalEntityStateException(Throwable cause) {
		super(cause);
	}
}
