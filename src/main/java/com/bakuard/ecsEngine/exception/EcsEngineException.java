package com.bakuard.ecsEngine.exception;

public class EcsEngineException extends RuntimeException {
	public EcsEngineException(String message) {
		super(message);
	}

	public EcsEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	public EcsEngineException(Throwable cause) {
		super(cause);
	}

	public EcsEngineException() {
	}
}
