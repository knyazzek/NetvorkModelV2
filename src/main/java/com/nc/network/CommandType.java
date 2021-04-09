package com.nc.network;

import com.nc.routeProviders.RouteProviderType;

public enum CommandType {
    ROUTE("route"),
    EXIT("exit");

    private String value;

    private CommandType(String value) {
        this.value = value;
    }

    public static CommandType fromString(String value) {
        if (value != null) {
            for (CommandType rpt : CommandType.values()) {
                if (value.equalsIgnoreCase(rpt.value)) {
                    return rpt;
                }
            }
        }
        return null;
    }
}