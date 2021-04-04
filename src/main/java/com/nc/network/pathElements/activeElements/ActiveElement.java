package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;
import com.nc.exceptions.NoPortsAvailableException;
import com.nc.network.pathElements.IPathElement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class ActiveElement implements IPathElement {
    private int id;
    private IpAddress ipAddress;
    private int timeDelay;
    private int costs;
    private Set<IPathElement> connections;
    private final int MAX_NUM_OF_CONNECTIONS;

    public ActiveElement(int id,
                         IpAddress ipAddress,
                         int timeDelay,
                         int costs,
                         int MAX_NUM_OF_CONNECTIONS) {
        this.id = id;
        this.ipAddress = new IpAddress(ipAddress);
        this.timeDelay = timeDelay;
        this.costs = costs;
        this.connections = new HashSet<IPathElement>();
        this.MAX_NUM_OF_CONNECTIONS = MAX_NUM_OF_CONNECTIONS;
    }

    @Override
    public int getId() {
        return id;
    }

    public IpAddress getIpAddress() {
        return new IpAddress(ipAddress);
    }

    @Override
    public int getTimeDelay() {
        return timeDelay;
    }

    @Override
    public int getCosts() {
        return costs;
    }

    @Override
    public Set<IPathElement> getConnections() {
        return new HashSet<IPathElement>(connections);
    }

    @Override
    public Set<IPathElement> getConnections(IPathElement sender) {
        Set<IPathElement> connectionsTmp = getConnections();
        connectionsTmp.remove(sender);
        return connectionsTmp;
    }

    @Override
    public void addConnection(IPathElement connection) throws NoPortsAvailableException {
        if (connections.size() < MAX_NUM_OF_CONNECTIONS)
            connections.add(connection);
        else
            throw new NoPortsAvailableException("Device has no available ports");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveElement that = (ActiveElement) o;
        return id == that.id &&
                timeDelay == that.timeDelay &&
                costs == that.costs &&
                MAX_NUM_OF_CONNECTIONS == that.MAX_NUM_OF_CONNECTIONS &&
                ipAddress.equals(that.ipAddress) &&
                connections.equals(that.connections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ipAddress, timeDelay, costs, connections, MAX_NUM_OF_CONNECTIONS);
    }

    @Override
    public String getInfo() {
        return "";
    }
}
