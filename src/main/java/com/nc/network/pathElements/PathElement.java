package com.nc.network.pathElements;

import com.nc.exceptions.NoPortsAvailableException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;

public abstract class PathElement implements IPathElement{
    private int id;
    private int timeDelay;
    private int costs;
    private int maxNumOfConnections;
    private Set<IPathElement> connections;

    public PathElement() {
        this.connections = new HashSet<>();
    }

    public PathElement(int timeDelay, int costs, int maxNumOfConnections) {
        this();
        this.timeDelay = timeDelay;
        this.costs = costs;
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
    public int getMaxNumOfConnections() {
        return maxNumOfConnections;
    }

    @Override
    public void setMaxNumOfConnections(int maxNumOfConnections) {
        this.maxNumOfConnections = maxNumOfConnections;
    }

    @Override
    public Set<IPathElement> getConnections() {
        return new HashSet<>(connections);
    }

    @Override
    public void addConnection(IPathElement pathElement) throws NoPortsAvailableException {
        if (connections.size() < maxNumOfConnections)
            connections.add(pathElement);
        else
            throw new NoPortsAvailableException("Device has no available ports");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(getId());
        out.writeInt(getTimeDelay());
        out.writeInt(getCosts());
        out.writeInt(getMaxNumOfConnections());
        out.writeInt(getConnections().size());

        for (IPathElement pathElement : getConnections()) {
            out.writeObject(pathElement);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readInt());
        setTimeDelay(in.readInt());
        setCosts(in.readInt());
        setMaxNumOfConnections(in.readInt());
        int connectionsCount = in.readInt();

        for (int i = 0; i < connectionsCount; i++) {
            try {
                addConnection((IPathElement)in.readObject());
            } catch (NoPortsAvailableException exception) {
                System.out.println("Element doesn't have available ports");
            }
        }
    }
}
