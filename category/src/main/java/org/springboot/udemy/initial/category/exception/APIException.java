package org.springboot.udemy.initial.category.exception;

public class APIException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
