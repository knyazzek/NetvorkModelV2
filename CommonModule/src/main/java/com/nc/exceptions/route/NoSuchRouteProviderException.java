package com.nc.exceptions.route;

public class NoSuchRouteProviderException extends Exception{
    public NoSuchRouteProviderException() {
    }

    public NoSuchRouteProviderException(String message) {
        super(message);
    }
}