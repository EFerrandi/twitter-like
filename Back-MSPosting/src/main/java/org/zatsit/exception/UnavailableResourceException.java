package org.zatsit.exception;

public class UnavailableResourceException extends RuntimeException {

    public UnavailableResourceException() {
        super();
    }

    public UnavailableResourceException(String message) {
        super(message);
    }

    public UnavailableResourceException(String message, Exception exception) {
        super(message, exception);
    }
}
