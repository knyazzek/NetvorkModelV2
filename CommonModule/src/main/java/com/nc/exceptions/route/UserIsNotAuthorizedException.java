package com.nc.exceptions.route;

public class UserIsNotAuthorizedException extends Exception {
    public UserIsNotAuthorizedException() {
        super();
    }

    public UserIsNotAuthorizedException(String message) {
        super(message);
    }
}
