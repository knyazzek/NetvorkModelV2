package com.nc.network.pathElements.activeElements;

public class Switch extends ActiveElement {
    private static final long serialVersionUID = 4L;

    public Switch(int id, IpAddress ipAddress, int timeDelay, int costs, int maxNumOfConnections) {
        super(ipAddress, timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public String toString() {
        return "Switch{" + getId() + "}";
    }

    @Override
    public String getInfo() {
        return "This is an active network device that " +
                "transfers data from one node to another within the same subnet";
    }
}
