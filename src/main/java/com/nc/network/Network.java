package com.nc.network;

import com.nc.IpAddress;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {
    private Map<Integer, IPathElement> pathElements;

    public Network() {
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

    public IPathElement getPathElementById(int id) {
        return pathElements.get(id);
    }

    public Map<Integer, IPathElement> getPathElements() {
        return new HashMap<Integer, IPathElement>(pathElements);
    }

    public void addPathElement(IPathElement pathElement) {
        pathElements.put(pathElements.size(), pathElement);
    }

    public void addPathElements(List<IPathElement> pathElements) {
        for (IPathElement pathElement : pathElements) {
            addPathElement(pathElement);
        }
    }

    @Override
    public String toString() {
        return "Network{" +
                "pathElements=" + pathElements +
                '}';
    }
}
