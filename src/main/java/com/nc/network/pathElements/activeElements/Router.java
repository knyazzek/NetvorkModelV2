package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;

public class Router extends ActiveElement{
    private static final long serialVersionUID = 3L;

    public Router(IpAddress ipAddress, int timeDelay, int costs, int maxNumOfConnections) {
        super(ipAddress, timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public String toString() {
        return "Router{" + getId() + "}";
    }
}
