package com.nc.routeProviders;

public enum RouteProviderType {
    MIN_TIME_DELAY_ROUTE_PROVIDER("MinTimeDelayRouteProvider"),
    MIN_COSTS_ROUTE_PROVIDER("MinCostsRouteProvider"),
    MIN_NODES_COUNT_ROUTE_PROVIDER("MinNodesCountRouteProvider");

    private String name;

    RouteProviderType(String name) {
        this.name = name;
    }

    public static RouteProviderType getEnum(String name) {
        switch (name) {
            case "MinTimeDelayRouteProvider":
                return MIN_TIME_DELAY_ROUTE_PROVIDER;
            case "MinCostsRouteProvider":
                return MIN_COSTS_ROUTE_PROVIDER;
            case "MinNodesCountRouteProvider":
                return MIN_NODES_COUNT_ROUTE_PROVIDER;
            default:
                return null;
        }
    }
}