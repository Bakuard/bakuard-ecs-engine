package com.bakuard.ecsEngine.exception;

public class UnregisteredSystemException extends EcsEngineException {

	public UnregisteredSystemException() {}

	public UnregisteredSystemException(String message) {
		super(message);
	}

	public UnregisteredSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnregisteredSystemException(Throwable cause) {
		super(cause);
	}
}
