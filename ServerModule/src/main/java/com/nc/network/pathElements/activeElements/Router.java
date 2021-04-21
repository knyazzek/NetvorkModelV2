package com.nc.network.pathElements.activeElements;

public class Router extends ActiveElement{
    private static final long serialVersionUID = 3L;

    public Router(IpAddress ipAddress, int timeDelay, int costs, int maxNumOfConnections) {
        super(ipAddress, timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public String toString() {
        return "Router{" + getId() + "}";
    }

    @Override
    public String getInfo() {
        return "This is an active network device that transfers data from one subnet to another.";
    }
}
