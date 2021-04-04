package com.nc.network.pathElements;

import com.nc.exceptions.NoPortsAvailableException;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

public interface IPathElement extends Externalizable{
    public int getId();
    public void setId(int id);
    public int getTimeDelay();
    public void setTimeDelay(int timeDelay);
    public int getCosts();
    public void setCosts(int costs);
    public Collection<IPathElement> getConnections();
    public Collection<IPathElement> getConnections(IPathElement sender);
    public void addConnection(IPathElement pathElement) throws NoPortsAvailableException;
    public int getMaxNumOfConnections();
    public void setMaxNumOfConnections(int num);
    public String getInfo();

    @Override
    default void writeExternal(ObjectOutput out) throws IOException {
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
    default void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
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
