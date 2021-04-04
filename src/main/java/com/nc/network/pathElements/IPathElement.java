package com.nc.network.pathElements;

import com.nc.exceptions.NoPortsAvailableException;

import java.util.Collection;

public interface IPathElement {
    public int getId();
    public int getTimeDelay();
    public int getCosts();
    public Collection<IPathElement> getConnections();
    public Collection<IPathElement> getConnections(IPathElement sender);
    public void addConnection(IPathElement pathElement) throws NoPortsAvailableException;
    public String getInfo();
}
