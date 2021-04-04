package com.nc.exceptions;

public class NoPortsAvailableException extends Exception{
    public NoPortsAvailableException() {
    }

    public NoPortsAvailableException(String message) {
        super(message);
    }

    public NoPortsAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}