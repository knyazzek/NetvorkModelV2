package com.nc.network;

public enum ClientCommandType {
    ROUTE("-route"),
    EXIT("-exit");

    private final String value;

    ClientCommandType(String value) {
        this.value = value;
    }

    public static ClientCommandType fromString(String value) {
        if (value != null) {
            for (ClientCommandType commandType : ClientCommandType.values()) {
                if (value.equalsIgnoreCase(commandType.value)) {
                    return commandType;
                }
            }
        }
        return null;
    }
}