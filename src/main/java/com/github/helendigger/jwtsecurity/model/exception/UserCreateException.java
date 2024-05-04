package com.github.helendigger.jwtsecurity.model.exception;

public class UserCreateException extends RuntimeException{
    public UserCreateException() {
    }

    public UserCreateException(String message) {
        super(message);
    }
}
