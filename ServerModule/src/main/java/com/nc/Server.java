package com.nc;

import com.nc.cli.CommonCommandLineParser;
import com.nc.cli.ServerCommandLineParser;
import com.nc.network.NetworkTest;
import com.nc.network.ServerCommandType;
import org.apache.commons.cli.ParseException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server {
    public final String UNIQUE_BINDING_NAME = "server.routing";
    private String loggedAdminName;

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (RemoteException e) {
            System.out.println("Error registering the remote server.");
        } catch (AlreadyBoundException e) {
            System.out.println("The registered name is already taken");
        }
    }

    public void start() throws RemoteException, AlreadyBoundException {
        NetworkTest networkTest = new NetworkTest();
        final Registry registry = LocateRegistry.createRegistry(2732);
        Remote stub = UnicastRemoteObject.exportObject(networkTest, 0);
        registry.bind(UNIQUE_BINDING_NAME, stub);
        CommonCommandLineParser commonCommandLineParser =
                new CommonCommandLineParser(networkTest);

        ServerCommandLineParser serverCommandLineParser =
                new ServerCommandLineParser(networkTest);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String str = scanner.nextLine();
            String[] commandLine = str.split(" ");
            ServerCommandType serverCommandType = ServerCommandType.fromString(commandLine[0]);

            if (serverCommandType == null) {
                System.out.println(commandLine[0] + " is not recognized as a command.");
                continue;
            }
            try {
                switch (serverCommandType) {
                    case CONFIG_FIREWALL:
                        serverCommandLineParser.firewallConfigParse(commandLine,
                                loggedAdminName);
                        break;

                    case LOGIN:
                        loggedAdminName = commonCommandLineParser.loginParse(commandLine);
                        break;

                    case LOGOUT:
                        commonCommandLineParser.logoutParse(commandLine,
                                loggedAdminName);
                        break;

                    case REGISTRATION:
                        commonCommandLineParser.registrationParse(commandLine);
                        break;

                    case SHUT_DOWN_SERVER:
                        networkTest.saveUsersData();
                        System.out.println("Shut down server");
                        System.exit(1);
                        break;
                }
            } catch (ParseException e) {
                System.out.println("Command line analysis error.");
            }
        }
    }
}