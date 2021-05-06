package com.nc;

import com.nc.exceptions.login.IncorrectLoginDataException;
import com.nc.exceptions.login.LoginIsAlreadyInUseException;
import com.nc.exceptions.logout.LogoutFromNotLoggedInUserException;
import com.nc.exceptions.registration.LoginIsAlreadyExistException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INetworkTest extends Remote {
    void route(String net,
               String routeProvider,
               String sender,
               String recipient,
               boolean isIp,
               boolean isOnlyActive,
               IClient client)
            throws RemoteException;

    String login(String login, String password)
            throws IncorrectLoginDataException,
            LoginIsAlreadyInUseException,
            RemoteException;

    void logout(String login)
            throws LogoutFromNotLoggedInUserException,
            RemoteException;

    void registration(String login, String password)
            throws LoginIsAlreadyExistException,
            RemoteException;

    void configFirewall(String netName,
                               String firewallIdStr,
                               String bannedElementIdStr,
                               String loggedAdminName,
                               boolean isDelete)
            throws RemoteException;
}
