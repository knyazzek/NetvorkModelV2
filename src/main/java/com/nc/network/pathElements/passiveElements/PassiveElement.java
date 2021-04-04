package com.nc.network.pathElements.passiveElements;

import com.nc.exceptions.NoPortsAvailableException;
import com.nc.network.pathElements.IPathElement;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class PassiveElement implements IPathElement {
    private int id;
    private int timeDelay;
    private int costs;
    private int maxNumOfConnections;
    private Set<IPathElement> connections;
    private static final long serialVersionUID = 6L;

    public PassiveElement() {
        this.connections = new HashSet<IPathElement>();
    }

    public PassiveElement(int timeDelay, int costs, int maxNumOfConnections) {
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

    @Override
    public int getTimeDelay() {
        return timeDelay;
    }

    @Override
    public void setTimeDelay(int timeDelay) {
        if (timeDelay >= 0)
            this.timeDelay = timeDelay;
    }

    @Override
    public int getCosts() {
        return costs;
    }

    public void setCosts(int costs) {
        if (costs >= 0)
            this.costs = costs;
    }

    @Override
    public Set<IPathElement> getConnections() {
        return new HashSet<IPathElement>(connections);
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
    public String getInfo() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassiveElement that = (PassiveElement) o;
        return id == that.id &&
                timeDelay == that.timeDelay &&
                costs == that.costs &&
                maxNumOfConnections == that.maxNumOfConnections &&
                Objects.equals(connections, that.connections);
    }
    @Override
    public int hashCode() {
        int res = id;
        res *= 31 + timeDelay;
        res *= 31 + costs;
        res *= 31 + connections.hashCode();
        res *= 31 + maxNumOfConnections;
        return res;
    }
}