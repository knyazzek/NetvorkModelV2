package com.nc.network;

public enum ServerCommandType {
    CONFIG_FIREWALL("-fconfig"),
    SHUT_DOWN_SERVER("-shutdown"),
    LOGIN("-login"),
    LOGOUT("-logout");

    private final String value;

    ServerCommandType(String value) {
        this.value = value;
    }

    public static ServerCommandType fromString(String value) {
        if (value != null) {
            for (ServerCommandType commandType : ServerCommandType.values()) {
                if (value.equalsIgnoreCase(commandType.value)) {
                    return commandType;
                }
            }
        }
        return null;
    }
}
