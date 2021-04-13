package com.nc.network.pathElements.passiveElements;

import com.nc.network.pathElements.PathElement;
import java.util.Objects;

public abstract class PassiveElement extends PathElement {
    private static final long serialVersionUID = 6L;

    public PassiveElement() {
        super();
    }

    public PassiveElement(int timeDelay, int costs, int maxNumOfConnections) {
        super(timeDelay, costs, maxNumOfConnections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassiveElement that = (PassiveElement) o;
        return getId() == that.getId() &&
                getTimeDelay() == that.getTimeDelay() &&
                getCosts() == that.getCosts() &&
                getMaxNumOfConnections() == that.getMaxNumOfConnections() &&
                Objects.equals(getConnections(), that.getConnections());
    }
    @Override
    public int hashCode() {
        int res = getId();
        res *= 31 + getTimeDelay();
        res *= 31 + getCosts();
        res *= 31 + getConnections().hashCode();
        res *= 31 + getMaxNumOfConnections();
        return res;
    }
}