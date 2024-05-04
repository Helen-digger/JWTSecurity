package com.github.helendigger.jwtsecurity.model.exception;

public class RefreshTokenException extends RuntimeException{
    public RefreshTokenException() {
    }

    public RefreshTokenException(String message) {
        super(message);
    }
}