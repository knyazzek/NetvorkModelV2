package com.nc.network.pathElements.passiveElements;

public class Hub extends PassiveElement {
    private static final long serialVersionUID = 8L;

    public Hub(int timeDelay, int costs, int maxNumOfConnections) {
        super(timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public String getInfo() {
        return "This is a passive network element designed to connect two or more path elements";
    }
}
