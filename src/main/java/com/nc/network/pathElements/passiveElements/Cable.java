package com.nc.network.pathElements.passiveElements;

import com.nc.exceptions.NoPortsAvailableException;
import com.nc.network.pathElements.IPathElement;

public class Cable extends PassiveElement {

    public Cable(int id, int timeDelay, int costs, IPathElement pe1, IPathElement pe2)
            throws NoPortsAvailableException {
        super(id, timeDelay, costs, 2);
        addConnection(pe1);
        addConnection(pe2);

        pe1.addConnection(this);
        pe1.addConnection(this);
    }

    @Override
    public void addConnection(IPathElement connection) {
        if (getConnections().size() < super.MAX_NUM_OF_CONNECTIONS) {
            super.addConnection(connection);
        }
    }

    @Override
    public String toString() {
        return "Cable{" + getId() + "}";
    }
}
