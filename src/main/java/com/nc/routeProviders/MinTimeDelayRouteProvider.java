package com.nc.routeProviders;

import com.nc.network.pathElements.IPathElement;

public class MinTimeDelayRouteProvider extends RouteProvider{

    public MinTimeDelayRouteProvider() {
        super((o1, o2) -> o1.getTimeDelay() - o2.getTimeDelay());
    }

    @Override
    public int valueOf(IPathElement pathElement) {
        return pathElement.getTimeDelay();
    }

    @Override
    public String getDescription() {
        return "MinTimeDelayRouteProvider finds the optimal route in terms of time delay. " +
                "It is based on Dijkstra's algorithm.";
    }
}
