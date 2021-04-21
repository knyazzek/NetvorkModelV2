package com.nc.network;

import com.nc.network.pathElements.IPathElement;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface INetworkTest extends Remote {
    List<IPathElement> route(String[] commands) throws RemoteException;
}
