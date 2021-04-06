package com.nc.routeProviders;

import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import java.util.List;

public interface IRouteProvider{
    List<IPathElement> getRouteByIds(int senderId, int recipientId, Network net)
            throws RouteNotFoundException;
    List<IPathElement> getRouteByIps(IpAddress senderIp, IpAddress recipientIp, Network net)
            throws RouteNotFoundException;
}
