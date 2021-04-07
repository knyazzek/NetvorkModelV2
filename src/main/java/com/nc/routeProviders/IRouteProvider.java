package com.nc.routeProviders;

import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import java.io.Externalizable;
import java.util.List;

public interface IRouteProvider extends Externalizable {
    List<IPathElement> getRoute(Network net, IPathElement sender, IPathElement recipient)
            throws RouteNotFoundException;
}