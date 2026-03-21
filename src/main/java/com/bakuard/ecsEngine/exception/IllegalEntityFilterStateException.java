package com.bakuard.ecsEngine.exception;

public class IllegalEntityFilterStateException extends EcsEngineException {
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
