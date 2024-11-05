package com.example.exception;

public class ExistedUsernameException extends Exception {
    public ExistedUsernameException(String message) {
        super(message);
    }
};

