package com.bakuard.ecsEngine.event;

public class UnknownEventQueueException extends RuntimeException {

    public UnknownEventQueueException() {}

    public UnknownEventQueueException(String message) {
        super(message);
    }

    public UnknownEventQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownEventQueueException(Throwable cause) {
        super(cause);
    }

}
