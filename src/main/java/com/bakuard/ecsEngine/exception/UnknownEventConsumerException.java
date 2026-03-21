package com.bakuard.ecsEngine.exception;

public class UnknownEventConsumerException extends EcsEngineException {

	public UnknownEventConsumerException() {}

	public UnknownEventConsumerException(String message) {
		super(message);
	}

	public UnknownEventConsumerException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownEventConsumerException(Throwable cause) {
		super(cause);
	}

}
