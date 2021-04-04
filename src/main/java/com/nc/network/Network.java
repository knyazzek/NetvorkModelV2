package com.nc.network;

import com.nc.IpAddress;
import com.nc.exceptions.ElementNotFoundException;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import sun.nio.ch.Net;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network implements Externalizable {
    private String name;
    private Map<Integer, IPathElement> pathElements;

    public Network() {
        pathElements = new HashMap<>();
    }

    public Network(String name) {
        this.name = name;
        this.pathElements = new HashMap<>();
    }

    public ActiveElement getPathElementByIp(IpAddress ipAddress) throws ElementNotFoundException {
        for (IPathElement pathElement : pathElements.values()) {
            if (pathElement instanceof ActiveElement) {
                ActiveElement activeElement = (ActiveElement) pathElement;

                if (activeElement.getIpAddress().equals(ipAddress))
                    return activeElement;
            }
        }
        throw  new ElementNotFoundException("The element with the specified Ip was not found");
    }

    public IPathElement getPathElementById(int id) {
        return pathElements.get(id);
    }

    public Map<Integer, IPathElement> getPathElements() {
        return new HashMap<Integer, IPathElement>(pathElements);
    }

    public void addPathElement(IPathElement pathElement) {
        pathElement.setId(pathElements.size());
        pathElements.put(pathElements.size(), pathElement);
    }

    public void addPathElements(List<IPathElement> pathElements) {
        for (IPathElement pathElement : pathElements) {
            addPathElement(pathElement);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeInt(pathElements.size());

        for (Map.Entry pathElement : pathElements.entrySet()) {
            out.writeInt((Integer) pathElement.getKey());
            out.writeObject(pathElement.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName((String) in.readObject());
        int pathElementsCount = in.readInt();

        for (int i = 0; i < pathElementsCount; i++) {
            int key  = in.readInt();
            IPathElement value = (IPathElement) in.readObject();
            pathElements.put(key, value);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Network{" +
                "pathElements=" + pathElements +
                '}';
    }
}
