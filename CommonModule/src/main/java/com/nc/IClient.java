package com.nc;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IClient extends Serializable, Remote {
    void printResponse(List<String> route) throws RemoteException;
    void printResponse(Throwable throwable) throws RemoteException;
}