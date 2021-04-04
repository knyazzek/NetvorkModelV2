package com.nc.routeProviders;

import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import java.util.List;

public interface RouteProvider {
    public List<IPathElement> getRouteByIds(int senderId, int recipientId, Network net)
            throws RouteNotFoundException;

    public List<IPathElement> getRouteByIps(int senderIp, int recipientIp, Network net)
            throws RouteNotFoundException;

}
