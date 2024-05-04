package com.github.helendigger.jwtsecurity.model.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
