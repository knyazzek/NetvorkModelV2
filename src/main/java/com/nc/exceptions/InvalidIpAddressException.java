package com.nc.exceptions;

public class InvalidIpAddressException extends Exception {
    public InvalidIpAddressException() {
        super();
    }

    public InvalidIpAddressException(String message) {
        super(message);
    }

    public InvalidIpAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIpAddressException(Throwable cause) {
        super(cause);
    }
}
