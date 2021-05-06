package com.nc.exceptions.registration;

public class LoginIsAlreadyExistException extends Exception{
    public LoginIsAlreadyExistException() {
    }

    public LoginIsAlreadyExistException(String message) {
        super(message);
    }
}
