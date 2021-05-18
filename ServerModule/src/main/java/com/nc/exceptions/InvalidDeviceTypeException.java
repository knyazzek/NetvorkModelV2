package com.nc.exceptions;

public class InvalidDeviceTypeException extends Exception{
    public InvalidDeviceTypeException() {
    }

    public InvalidDeviceTypeException(String message) {
        super(message);
    }

    public InvalidDeviceTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
