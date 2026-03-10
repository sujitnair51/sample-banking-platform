package com.snbnk.onboarding.error;

public class ConflictException extends RuntimeException{
    public ConflictException(String message) {
        super(message);
    }
}
