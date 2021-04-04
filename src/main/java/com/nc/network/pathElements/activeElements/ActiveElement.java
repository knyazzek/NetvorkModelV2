package com.nc.network.pathElements.activeElements;

import com.nc.IpAddress;
import com.nc.exceptions.NoPortsAvailableException;
import com.nc.network.pathElements.IPathElement;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class ActiveElement implements IPathElement {
    private int id;
    private IpAddress ipAddress;
    private int timeDelay;
    private int costs;
    private Set<IPathElement> connections;
    private int maxNumOfConnections;
    private static final long serialVersionUID = 1L;

    public ActiveElement() {
        this.connections = new HashSet<IPathElement>();
    }

    public ActiveElement(IpAddress ipAddress,
                         int timeDelay,
                         int costs,
                         int maxNumOfConnections) {
        this.ipAddress = new IpAddress(ipAddress);
        this.timeDelay = timeDelay;
        this.costs = costs;
        this.connections = new HashSet<IPathElement>();
        this.maxNumOfConnections = maxNumOfConnections;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public IpAddress getIpAddress() {
        return new IpAddress(ipAddress);
    }

    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public int getTimeDelay() {
        return timeDelay;
    }

    @Override
    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    @Override
    public int getCosts() {
        return costs;
    }

    @Override
    public void setCosts(int costs) {
        this.costs = costs;
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
        if (connections.size() < maxNumOfConnections)
            connections.add(connection);
        else
            throw new NoPortsAvailableException("Device has no available ports");
    }

    @Override
    public int getMaxNumOfConnections() {
        return maxNumOfConnections;
    }

    @Override
    public void setMaxNumOfConnections(int maxNumOfConnections) {
        this.maxNumOfConnections = maxNumOfConnections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveElement that = (ActiveElement) o;
        return id == that.id &&
                timeDelay == that.timeDelay &&
                costs == that.costs &&
                maxNumOfConnections == that.maxNumOfConnections &&
                ipAddress.equals(that.ipAddress) &&
                connections.equals(that.connections);
    }

    @Override
    public int hashCode() {
        int res = id;
        res *= 31 + timeDelay;
        res *= 31 + costs;
        res *= 31 + maxNumOfConnections;
        return res;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        IPathElement.super.writeExternal(out);
        out.writeObject(ipAddress);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        IPathElement.super.readExternal(in);
        ipAddress = (IpAddress)in.readObject();
    }
}
