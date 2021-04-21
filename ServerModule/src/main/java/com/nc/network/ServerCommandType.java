package com.nc.network;

public enum ServerCommandType {
    CONFIG_FIREWALL("-f","-fconfig"),
    SHUT_DOWN_SERVER("-s","-shutdown"),
    LOGIN("-l","-login"),
    LOGOUT("-o","-logout"),
    REGISTRATION("-r","-registration");

    private final String shortValue;
    private final String longValue;

    ServerCommandType(String shortValue, String longValue) {
        this.shortValue = shortValue;
        this.longValue = longValue;
    }

    public static ServerCommandType fromString(String value) {
        if (value != null) {
            for (ServerCommandType commandType : ServerCommandType.values()) {
                if (value.equalsIgnoreCase(commandType.shortValue)
                    || value.equalsIgnoreCase(commandType.longValue)) {
                    return commandType;
                }
            }
        }
        return null;
    }
}
