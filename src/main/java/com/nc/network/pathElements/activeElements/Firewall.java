package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;
import java.util.List;
import java.util.Set;

public class Firewall extends ActiveElement {
    private Set<IpAddress> bannedIpAddresses;

    public Firewall(int id, IpAddress ipAddress, int timeDelay, int costs, int MAX_NUM_OF_CONNECTIONS) {
        super(id, ipAddress, timeDelay, costs, MAX_NUM_OF_CONNECTIONS);
    }

    public void addBannedIpAddress(IpAddress ipAddress) {
        bannedIpAddresses.add(ipAddress);
    }

    public void addBannedIpAddresses(List<IpAddress> ipAddresses) {
        for (IpAddress ipAddress : ipAddresses) {
            bannedIpAddresses.add(ipAddress);
        }
    }
}
