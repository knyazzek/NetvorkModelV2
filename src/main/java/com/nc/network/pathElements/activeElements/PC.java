package com.nc.network.pathElements.activeElements;

public class PC extends ActiveElement{
    private static final long serialVersionUID = 2L;

    public PC() {}

    public PC(IpAddress ipAddress, int timeDelay, int costs, int maxNumOfConnections) {
        super(ipAddress, timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public String toString() {
        return "PC{" + getId() + "}";
    }
}
