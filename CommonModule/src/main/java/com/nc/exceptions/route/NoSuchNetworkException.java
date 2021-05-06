package com.nc.exceptions.route;

public class NoSuchNetworkException extends Exception {
    public NoSuchNetworkException() {
    }

    public NoSuchNetworkException(String message) {
        super(message);
    }
}