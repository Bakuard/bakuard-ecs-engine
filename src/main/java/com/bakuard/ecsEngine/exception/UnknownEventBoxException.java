package com.bakuard.ecsEngine.exception;

public class UnknownEventBoxException extends EcsEngineException {

	public UnknownEventBoxException() {}

	public UnknownEventBoxException(String message) {
		super(message);
	}

	public UnknownEventBoxException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownEventBoxException(Throwable cause) {
		super(cause);
	}

}
