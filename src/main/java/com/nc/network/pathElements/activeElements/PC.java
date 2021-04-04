package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;

public class PC extends ActiveElement{

    public PC(int id, IpAddress ipAddress, int timeDelay, int costs, int MAX_NUM_OF_CONNECTIONS) {
        super(id, ipAddress, timeDelay, costs, MAX_NUM_OF_CONNECTIONS);
    }

    @Override
    public String toString() {
        return "PC{" + getId() + "}";
    }
}
