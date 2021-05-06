package com.nc.exceptions.login;

public class LoginIsAlreadyInUseException extends Exception {
    public LoginIsAlreadyInUseException() {
    }

    public LoginIsAlreadyInUseException(String message) {
        super(message);
    }
}
