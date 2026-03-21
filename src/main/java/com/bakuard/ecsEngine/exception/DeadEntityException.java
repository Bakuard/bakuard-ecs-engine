package com.bakuard.ecsEngine.exception;

public class DeadEntityException extends EcsEngineException {
	public DeadEntityException(String message) {
		super(message);
	}

	public DeadEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeadEntityException(Throwable cause) {
		super(cause);
	}

	public DeadEntityException() {}
}
