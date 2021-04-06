package com.nc.routeProviders;

import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import java.util.List;

public interface IRouteProvider{
    List<IPathElement> getRoute(Network net, IPathElement sender, IPathElement recipient)
            throws RouteNotFoundException;
    //TODO I'm not sure about the correctness of this decision
    IPathElement getRecipient();
    void setRecipient(IPathElement recipient);
}
