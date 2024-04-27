package com.bakuard.ecsEngine.system;

public class UnknownGroupException extends RuntimeException {

    public UnknownGroupException() {}

    public UnknownGroupException(String message) {
        super(message);
    }

    public UnknownGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownGroupException(Throwable cause) {
        super(cause);
    }
}
