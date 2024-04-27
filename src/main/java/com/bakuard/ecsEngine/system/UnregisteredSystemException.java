package com.bakuard.ecsEngine.system;

public class UnregisteredSystemException extends RuntimeException {

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
