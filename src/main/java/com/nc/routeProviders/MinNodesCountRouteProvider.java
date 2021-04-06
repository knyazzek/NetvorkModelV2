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
}
