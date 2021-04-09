package com.nc.routeProviders;

public enum RouteProviderType {
    MIN_TIME_DELAY_ROUTE_PROVIDER("MinTimeDelayRouteProvider"),
    MIN_COSTS_ROUTE_PROVIDER("MinCostsRouteProvider"),
    MIN_NODES_COUNT_ROUTE_PROVIDER("MinNodesCountRouteProvider");

    private String value;

    private RouteProviderType(String value) {
        this.value = value;
    }

    public static RouteProviderType fromString(String value) {
        if (value != null) {
            for (RouteProviderType rpt : RouteProviderType.values()) {
                if (value.equalsIgnoreCase(rpt.value)) {
                    return rpt;
                }
            }
        }
        return null;
    }
}