package org.zatsit.exception;


public class InvalidDtoException extends RuntimeException {

    public InvalidDtoException() {
        super();
    }

    public InvalidDtoException(String message) {
        super(message);
    }

    public InvalidDtoException(String message, Exception exception) {
        super(message, exception);
    }
}
