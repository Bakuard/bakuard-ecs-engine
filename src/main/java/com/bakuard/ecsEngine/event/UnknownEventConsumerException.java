package com.bakuard.ecsEngine.event;

public class UnknownEventConsumerException extends RuntimeException {

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
