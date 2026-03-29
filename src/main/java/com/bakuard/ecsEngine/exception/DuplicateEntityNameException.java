package com.bakuard.ecsEngine.exception;

public class DuplicateEntityNameException extends EcsEngineException {
	public DuplicateEntityNameException() {
	}

	public DuplicateEntityNameException(String message) {
		super(message);
	}

	public DuplicateEntityNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateEntityNameException(Throwable cause) {
		super(cause);
	}
}
