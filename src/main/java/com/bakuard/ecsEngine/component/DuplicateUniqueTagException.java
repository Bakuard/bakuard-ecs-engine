package com.bakuard.ecsEngine.component;

public class DuplicateUniqueTagException extends RuntimeException {
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
