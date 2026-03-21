package com.bakuard.ecsEngine.exception;

public class DuplicateUniqueTagException extends EcsEngineException {
	public DuplicateUniqueTagException() {
	}

	public DuplicateUniqueTagException(String message) {
		super(message);
	}

	public DuplicateUniqueTagException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateUniqueTagException(Throwable cause) {
		super(cause);
	}
}
