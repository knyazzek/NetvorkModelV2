package com.nc.network;

import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class Network implements Externalizable {
    private static final long serialVersionUID = 10L;
    private String name;
    private final Map<Integer, IPathElement> pathElements;

    public Network() {
        pathElements = new HashMap<>();
    }

    public Network(String name) {
        this.name = name;
        this.pathElements = new HashMap<>();
    }

    public ActiveElement getPathElementByIp(IpAddress ipAddress) {
        for (IPathElement pathElement : pathElements.values()) {
            if (pathElement instanceof ActiveElement) {
                ActiveElement activeElement = (ActiveElement) pathElement;

                if (activeElement.getIpAddress().equals(ipAddress))
                    return activeElement;
            }
        }
        return null;
    }

    public ActiveElement getPathElementById(int id) {
        IPathElement element = pathElements.get(id);

        if (element instanceof ActiveElement) {
            return (ActiveElement) element;
        }

        return null;
    }

    public Map<Integer, IPathElement> getPathElements() {
        return new HashMap<>(pathElements);
    }

    public void addPathElement(IPathElement pathElement) {
        pathElement.setId(pathElements.size());
        pathElements.put(pathElements.size(), pathElement);
        pathElement.setNetwork(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeInt(pathElements.size());

        for (Map.Entry<Integer, IPathElement> pathElement : pathElements.entrySet()) {
            out.writeInt(pathElement.getKey());
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

    public void refreshAllCachedRouteProviders() {
        System.out.println("Refresh all cached route providers");

        for (IPathElement pathElement : pathElements.values()) {
            if (pathElement instanceof ActiveElement) {
                ((ActiveElement) pathElement).setHasActualRouteProvider(false);
            }
        }
    }

    @Override
    public String toString() {
        return "Network{" +
                "name=" + name +
                ", pathElements=" + pathElements +
                '}';
    }
}
