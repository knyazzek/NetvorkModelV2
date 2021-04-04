package com.nc.network.pathElements.passiveElements;

import com.nc.network.pathElements.IPathElement;
import java.util.HashSet;
import java.util.Set;

public class PassiveElement implements IPathElement {
    private int id;
    private int timeDelay;
    private int costs;
    private Set<IPathElement> connections;
    public final int MAX_NUM_OF_CONNECTIONS;

    public PassiveElement(int id, int timeDelay, int costs, int MAX_NUM_OF_CONNECTIONS) {
        this.id = id;
        this.timeDelay = timeDelay;
        this.costs = costs;
        this.connections = new HashSet<IPathElement>();
        this.MAX_NUM_OF_CONNECTIONS = MAX_NUM_OF_CONNECTIONS;
    }

    @Override
    public int getId() {
        return id;
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
    public void addConnection(IPathElement connection) {
        connections.add(connection);
    }

    @Override
    public String getInfo() {
        return "";
    }
}