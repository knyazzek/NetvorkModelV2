package com.nc.cli;

public enum ClientCommandType {
    LOGIN("-l", "-login"),
    LOGOUT("-o", "-logout"),
    REGISTRATION("-r", "-registration"),
    ROUTE("-r","-route"),
    EXIT("-e","-exit");

    private final String shortValue;
    private final String longValue;

    ClientCommandType(String shortValue, String longValue) {
        this.shortValue = shortValue;
        this.longValue = longValue;
    }

    public static ClientCommandType fromString(String value) {
        if (value != null) {
            for (ClientCommandType commandType : ClientCommandType.values()) {
                if (value.equalsIgnoreCase(commandType.shortValue)
                    || value.equalsIgnoreCase(commandType.longValue)) {
                    return commandType;
                }
            }
        }
        return null;
    }
}