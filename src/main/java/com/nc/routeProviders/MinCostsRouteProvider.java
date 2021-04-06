package com.nc.routeProviders;

import com.nc.network.pathElements.IPathElement;

public class MinCostsRouteProvider extends RouteProvider{

    public MinCostsRouteProvider() {
        super((o1, o2) -> o1.getCosts() - o2.getCosts());
    }

    @Override
    public int valueOf(IPathElement pathElement) {
        return pathElement.getCosts();
    }
}
