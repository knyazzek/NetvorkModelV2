package com.nc;

import com.nc.cli.ClientCommandLineParser;
import com.nc.cli.ClientCommandType;
import com.nc.cli.CommonCommandLineParser;
import com.nc.exceptions.route.ElementNotFoundException;
import com.nc.exceptions.route.NoSuchNetworkException;
import com.nc.exceptions.route.NoSuchRouteProviderException;
import org.apache.commons.cli.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class Client extends UnicastRemoteObject implements IClient {
    private static final String SERVER_BINDING_NAME = "server.routing";
    private String loggedClientName;

    protected Client() throws RemoteException {}

    public static void main(String[] args) {
        try {
            new Client().start();
        } catch (RemoteException e) {
            System.out.println("Server startup error. Try again later.");
            System.exit(1);
        }
    }

    private void start() {
        try {
            Registry registry = LocateRegistry.getRegistry(2732);
            INetworkTest networkTest = (INetworkTest) registry.lookup(SERVER_BINDING_NAME);
            CommonCommandLineParser commonCommandLineParser =
                    new CommonCommandLineParser(networkTest);

            ClientCommandLineParser clientCommandLineParser =
                    new ClientCommandLineParser(networkTest);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                String str = scanner.nextLine();
                String[] commandLine = str.split(" ");
                ClientCommandType clientCommandType =
                        ClientCommandType.fromString(commandLine[0]);

                if (clientCommandType == null) {
                    System.out.println(commandLine[0] + " is not recognized as a command.");
                    continue;
                }

                try {
                    switch (clientCommandType) {
                        case LOGIN:
                            loggedClientName = commonCommandLineParser
                                    .loginParse(commandLine);
                            break;

                        case LOGOUT:
                            commonCommandLineParser.logoutParse(commandLine,
                                    loggedClientName);
                            break;

                        case REGISTRATION:
                            commonCommandLineParser.registrationParse(commandLine);
                            break;

                        case ROUTE:
                            clientCommandLineParser.routeParse(commandLine,
                                    loggedClientName, this);
                            break;

                        case EXIT:
                            System.out.println("Exit.");
                            System.exit(0);
                            break;
                    }
                } catch (ParseException e) {
                    System.out.println("Command line analysis error.");
                }
            }
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Connection to the server failed. Try again later.");
            System.exit(1);
        }
    }

    @Override
    public void printResponse(List<String> route) {
        System.out.println(route);
    }

    @Override
    public void printResponse(Throwable throwable) {
        if (throwable.getClass() == NoSuchRouteProviderException.class) {
            System.out.println("Route provider with specified name not found.");
        } else if (throwable.getClass() == ElementNotFoundException.class) {
            System.out.println("Element(s) with specified parameter(s) not found(s).");
        } else if (throwable.getClass() == NoSuchNetworkException.class) {
            System.out.println("Network with specified name not found.");
        } else {
            System.out.println("Unknown error. Try again later.");
        }
    }
}