package com.nc.routeProviders;

public class RouteProviderFactory {
    public IRouteProvider createRouteProvider(RouteProviderType type) {
        IRouteProvider routeProvider = null;

        switch (type) {
            case MIN_COSTS_ROUTE_PROVIDER:
                routeProvider = new MinCostsRouteProvider();
                break;
            case MIN_TIME_DELAY_ROUTE_PROVIDER:
                routeProvider = new MinTimeDelayRouteProvider();
                break;
            case MIN_NODES_COUNT_ROUTE_PROVIDER:
                routeProvider = new MinNodesCountRouteProvider();
                break;
        }
        return routeProvider;
    }
}
