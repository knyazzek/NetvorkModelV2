package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;
import com.nc.network.pathElements.IPathElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Firewall extends ActiveElement {
    private Set<IPathElement> bannedElements;

    public Firewall(int id, IpAddress ipAddress, int timeDelay, int costs, int MAX_NUM_OF_CONNECTIONS) {
        super(id, ipAddress, timeDelay, costs, MAX_NUM_OF_CONNECTIONS);
        //TODO override equals
        bannedElements = new HashSet<>();
    }

    public Set<IPathElement> getBannedElements() {
        return new HashSet<>(bannedElements);
    }

    public void addBannedElement(IPathElement bannedElement) {
        bannedElements.add(bannedElement);
    }

    public void addBannedIpAddresses(List<IPathElement> bannedElements) {
        for (IPathElement bannedElement : bannedElements) {
            bannedElements.add(bannedElement);
        }
    }

    @Override
    public String toString() {
        return "Firewall{" +
                getId() +
                '}';
    }
}
