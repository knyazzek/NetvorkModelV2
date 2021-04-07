package com.nc.network.pathElements.activeElements;

import com.nc.network.pathElements.PathElement;
import com.nc.routeProviders.RouteProvider;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class ActiveElement extends PathElement {
    private IpAddress ipAddress;
    private RouteProvider cachedRouteProvider;
    private boolean hasActualRouteProvider;
    private static final long serialVersionUID = 1L;

    public ActiveElement() {
        super();
    }

    public ActiveElement(IpAddress ipAddress,
                         int timeDelay,
                         int costs,
                         int maxNumOfConnections) {
        super(timeDelay, costs, maxNumOfConnections);
        this.ipAddress = new IpAddress(ipAddress);
    }

    public IpAddress getIpAddress() {
        return new IpAddress(ipAddress);
    }

    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public RouteProvider getCachedRouteProvider() {
        return cachedRouteProvider;
    }

    public void setCachedRouteProvider(RouteProvider cachedRouteProvider) {
        this.cachedRouteProvider = cachedRouteProvider;
    }

    public boolean hasActualRouteProvider() {
        return hasActualRouteProvider;
    }

    public void setHasActualRouteProvider(boolean hasActualRouteProvider) {
        this.hasActualRouteProvider = hasActualRouteProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveElement that = (ActiveElement) o;
        return getId() == that.getId() &&
                getTimeDelay() == that.getTimeDelay() &&
                getCosts() == that.getCosts() &&
                getMaxNumOfConnections() == that.getMaxNumOfConnections() &&
                ipAddress.equals(that.ipAddress) &&
                getConnections().equals(that.getConnections());
    }

    @Override
    public int hashCode() {
        int res = getId();
        res *= 31 + getTimeDelay();
        res *= 31 + getCosts();
        res *= 31 + getMaxNumOfConnections();
        return res;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(ipAddress);
        out.writeObject(cachedRouteProvider);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        ipAddress = (IpAddress)in.readObject();
        cachedRouteProvider = (RouteProvider)in.readObject();
    }
}
