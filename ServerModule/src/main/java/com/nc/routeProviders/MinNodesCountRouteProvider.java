package com.nc.routeProviders;

import com.nc.network.pathElements.IPathElement;

public class MinNodesCountRouteProvider extends RouteProvider{

    public MinNodesCountRouteProvider() {
        super((o1, o2) -> 0);
    }

    @Override
    public int valueOf(IPathElement pathElement) {
        return 1;
    }

    @Override
    public String getDescription() {
        return "MinNodesCountRouteProvider finds the route with the minimum number of nodes. " +
                "It is based on Dijkstra's algorithm.";
    }
}
