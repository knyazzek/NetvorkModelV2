package com.nc.network.pathElements;

import com.nc.exceptions.NoPortsAvailableException;
import java.io.Externalizable;
import java.util.Collection;

public interface IPathElement extends Externalizable{
    int getId();
    void setId(int id);
    int getTimeDelay();
    void setTimeDelay(int timeDelay);
    int getCosts();
    void setCosts(int costs);
    Collection<IPathElement> getConnections();
    void addConnection(IPathElement pathElement) throws NoPortsAvailableException;
    int getMaxNumOfConnections();
    void setMaxNumOfConnections(int num);
    String getInfo();
}
