package com.nc.exceptions.login;

public class IncorrectLoginDataException extends Exception {
    public IncorrectLoginDataException() {
    }

    public IncorrectLoginDataException(String message) {
        super(message);
    }
}
