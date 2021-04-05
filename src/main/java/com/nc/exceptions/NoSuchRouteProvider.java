package com.nc.exceptions;

public class NoSuchRouteProvider extends Exception{
    public NoSuchRouteProvider() {
    }

    public NoSuchRouteProvider(String message) {
        super(message);
    }

    public NoSuchRouteProvider(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchRouteProvider(Throwable cause) {
        super(cause);
    }
}
