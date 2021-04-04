package com.nc.routeProviders;

import com.nc.network.pathElements.IPathElement;

public class MinTimeDelayRouteProvider extends RouteProvider{
    private static MinTimeDelayRouteProvider instance;

    private MinTimeDelayRouteProvider() {
        super((o1, o2) -> o1.getTimeDelay() - o2.getTimeDelay());
    }

    public static MinTimeDelayRouteProvider getInstance() {
        if (instance == null)
            instance = new MinTimeDelayRouteProvider();

        return instance;
    }

    @Override
    public int valueOf(IPathElement pathElement) {
        return pathElement.getTimeDelay();
    }
}
