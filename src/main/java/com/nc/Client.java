package com.nc;

import com.nc.network.ClientCommandType;
import com.nc.network.INetworkTest;
import com.nc.network.pathElements.IPathElement;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static final String UNIQUE_BINDING_NAME = "server.routing";

    public static void main(String[] args) {
        try {
            final Registry registry = LocateRegistry.getRegistry(2732);
            INetworkTest networkTest = (INetworkTest) registry.lookup(UNIQUE_BINDING_NAME);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Input:");
                String str = scanner.nextLine();
                String[] commandLine = str.split(" ");
                ClientCommandType clientCommandType = ClientCommandType.fromString(commandLine[0]);

                if (clientCommandType == null) {
                    System.out.println(commandLine[0] + " is not recognized as a command.");
                    continue;
                }

                switch (clientCommandType) {
                    case ROUTE :
                        try {
                            List<IPathElement> res = networkTest.route(commandLine);
                            if (res != null) {
                                System.out.println(res);
                            } else {
                                System.out.println("Couldn't get the route.");
                            }
                        } catch (RemoteException e) {
                            System.out.println("Error calling the remote method.");
                            e.printStackTrace();
                        }
                        break;

                    case EXIT :
                        System.out.println("Exit.");
                        System.exit(0);
                        break;
                }
            }
        } catch (RemoteException e) {
            System.out.println("Connection to the remote server failed");
        } catch (NotBoundException e) {
            System.out.println("No binding found named as \"" + UNIQUE_BINDING_NAME + "\"");
        }
    }
}
