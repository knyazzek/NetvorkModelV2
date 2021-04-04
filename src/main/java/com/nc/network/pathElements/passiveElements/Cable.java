package com.nc.network.pathElements.passiveElements;

import com.nc.exceptions.NoPortsAvailableException;
import com.nc.network.pathElements.IPathElement;

public class Cable extends PassiveElement {
    private static final long serialVersionUID = 7L;

    public Cable() {}

    public Cable(int timeDelay, int costs, IPathElement pe1, IPathElement pe2)
            throws NoPortsAvailableException {
        super(timeDelay, costs, 2);
        addConnection(pe1);
        addConnection(pe2);

        pe1.addConnection(this);
        pe1.addConnection(this);
    }

    @Override
    public String toString() {
        return "Cable{" + getId() + "}";
    }
}
