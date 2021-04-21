package com.nc;

import com.nc.network.NetworkTest;
import com.nc.network.ServerCommandType;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
    public final String UNIQUE_BINDING_NAME = "server.routing";
    //TODO serializable admin's data and fixed form of password storing.
    private final Map<String, String> admins = new HashMap<>();
    private boolean isAuthorized;
    private final Options options;
    private final CommandLineParser parser;
    private NetworkTest server;

    public Server() {
        this.options = getOptions();
        this.parser = new DefaultParser();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.admins.put("admin","password");
        try {
            server.start();
        } catch (RemoteException e) {
            System.out.println("Error registering the remote server.");
        } catch (AlreadyBoundException e) {
            System.out.println("The registered name is already taken");
        }
    }

    public void start() throws RemoteException, AlreadyBoundException {
        server = new NetworkTest();
        final Registry registry = LocateRegistry.createRegistry(2732);
        Remote stub = UnicastRemoteObject.exportObject(server, 0);
        registry.bind(UNIQUE_BINDING_NAME, stub);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Input:");
            String str = scanner.nextLine();
            String[] commandLine = str.split(" ");
            ServerCommandType serverCommandType = ServerCommandType.fromString(commandLine[0]);

            if (serverCommandType == null) {
                System.out.println(commandLine[0] + " is not recognized as a command.");
                continue;
            }

            switch (serverCommandType) {
                case CONFIG_FIREWALL:
                    System.out.println("CONFIG_FIREWALL");
                    configFirewall(commandLine);
                    break;

                case LOGIN:
                    login(commandLine);
                    break;

                case LOGOUT:
                    System.out.println("Logout.");
                    logout();
                    break;

                case SHUT_DOWN_SERVER:
                    System.out.println("Shut down server");
                    System.exit(1);
                    break;
            }
        }
    }

    private void configFirewall(String[] args) {
        if (!isAuthorized) {
            System.out.println("You should authorize to config firewall.");
            return;
        }

        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] configFirewallArgs = commandLine.getOptionValues("fconfig");

            String netName = configFirewallArgs[0];
            String firewallIdStr = configFirewallArgs[1];
            String bannedElementIdStr = configFirewallArgs[2];

            if (!StringUtils.isNumeric(firewallIdStr)
                    || !StringUtils.isNumeric(bannedElementIdStr)) {
                System.out.println("The element Ids must be numbers");
                return;
            }

            Integer firewallId = Integer.parseInt(firewallIdStr);
            Integer bannedElementId = Integer.parseInt(bannedElementIdStr);
            boolean isDelete = commandLine.hasOption("del");

            server.changeBannedList(netName, firewallId, bannedElementId, isDelete);
            server.refreshAllCachedRouteProvidersOf(netName);

        } catch (ParseException e) {
            System.out.println("Incorrect data for command \"" + args[0] + "\".");
        }
    }

    private void login(String[] args) {
        if (isAuthorized) {
            System.out.println("You are already logged in.");
            return;
        }

        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] loginArgs = commandLine.getOptionValues("login");

            String loginName = loginArgs[0];
            String password = loginArgs[1];

            if (admins.containsKey(loginName) && admins.get(loginName).equals(password)) {
                System.out.println("Correct login data entered.");
                isAuthorized = true;
            } else {
                System.out.println("Incorrect login or password entered.");
            }
        } catch (ParseException e) {
            System.out.println("Incorrect data for command \" " + args[0] + " \".");
        }
    }

    private void logout() {
        if (isAuthorized) {
            isAuthorized = false;
        } else {
            System.out.println("You are not logged in to log out.");
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        Option configFirewallOption = Option.builder("f")
                .longOpt("fconfig")
                .required(false)
                .numberOfArgs(3)
                .build();

        Option loginOption = Option.builder("l")
                .longOpt("login")
                .required(false)
                .numberOfArgs(2)
                .build();

        Option deleteBannedElementOption = Option.builder("d")
                .longOpt("del")
                .required(false)
                .hasArg(false)
                .build();

        options.addOption(configFirewallOption)
                .addOption(loginOption)
                .addOption(deleteBannedElementOption);

        return options;
    }

    //TODO add admin
    public void addAdmin() {

    }
}