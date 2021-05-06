package com.nc.exceptions.logout;

public class LogoutFromNotLoggedInUserException extends Exception {
    public LogoutFromNotLoggedInUserException() {
    }

    public LogoutFromNotLoggedInUserException(String message) {
        super(message);
    }
}
