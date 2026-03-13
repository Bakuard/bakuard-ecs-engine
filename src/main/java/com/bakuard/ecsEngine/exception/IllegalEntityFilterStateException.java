package com.bakuard.ecsEngine.exception;

public class IllegalEntityFilterStateException extends RuntimeException {
	public IllegalEntityFilterStateException(String message) {
		super(message);
	}

	public IllegalEntityFilterStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalEntityFilterStateException(Throwable cause) {
		super(cause);
	}

	public IllegalEntityFilterStateException() {
	}
}
