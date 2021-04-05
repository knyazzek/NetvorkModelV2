package com.nc.routeProviders;

import com.nc.network.pathElements.IPathElement;

public class MinNodesCountRouteProvider extends RouteProvider{
    private static MinNodesCountRouteProvider instance;

    public MinNodesCountRouteProvider() {
        super((o1, o2) -> 0);
    }

    public static MinNodesCountRouteProvider getInstance() {
        if (instance == null)
            instance = new MinNodesCountRouteProvider();

        return instance;
    }

    @Override
    public int valueOf(IPathElement pathElement) {
        return 1;
    }
}
