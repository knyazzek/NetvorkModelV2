package com.nc.routeProviders;

import com.nc.network.pathElements.IPathElement;

public class MinCostsRouteProvider extends RouteProvider{
    private static MinCostsRouteProvider instance;

    private MinCostsRouteProvider() {
        super((o1, o2) -> o1.getCosts() - o2.getCosts());
    }

    public static MinCostsRouteProvider getInstance() {
        if (instance == null)
            instance = new MinCostsRouteProvider();

        return instance;
    }

    @Override
    public int valueOf(IPathElement pathElement) {
        return pathElement.getCosts();
    }
}
