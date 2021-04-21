package com.nc;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface INetworkTest extends Remote {
    List<String> route(String[] commands) throws RemoteException;
}
