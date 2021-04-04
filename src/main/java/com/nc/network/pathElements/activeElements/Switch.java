package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;

public class Switch extends ActiveElement {
    private static final long serialVersionUID = 4L;

    public Switch(int id, IpAddress ipAddress, int timeDelay, int costs, int maxNumOfConnections) {
        super(ipAddress, timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public String toString() {
        return "Switch{" + getId() + "}";
    }
}
