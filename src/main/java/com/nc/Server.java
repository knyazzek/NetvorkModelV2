package com.nc;

import com.nc.network.NetworkTest;
import com.nc.network.ServerCommandType;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private Map<String, String> admins;
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
        server.loadAdminsData();

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
                    System.out.println("Firewall configuration started.");
                    configFirewall(commandLine);
                    break;

                case LOGIN:
                    login(commandLine);
                    break;

                case LOGOUT:
                    System.out.println("Logout completed.");
                    logout();
                    break;

                case SHUT_DOWN_SERVER:
                    saveAdminsData();
                    System.out.println("Shut down server");
                    System.exit(1);
                    break;
                case REGISTRATION:
                    adminRegistration(commandLine);
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
            System.out.println("Firewall configuration completed successfully.");

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

            if (admins.containsKey(loginName) && BCrypt.checkpw(password, admins.get(loginName))) {
                System.out.println("Login completed.");
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

        Option registrationOption = Option.builder("r")
                .longOpt("registration")
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
                .addOption(deleteBannedElementOption)
                .addOption(registrationOption);

        return options;
    }

    public void adminRegistration(String[] args) {
        if (isAuthorized) {
            try {
                CommandLine commandLine = parser.parse(options, args);
                String[] registrationArgs = commandLine.getOptionValues("registration");

                String login = registrationArgs[0];
                String password = registrationArgs[1];

                if (admins.containsKey(login)) {
                    System.out.println("Specified login is already exist.");
                    return;
                }

                if (password.length() < 6) {
                    System.out.println("The password must be at least 6 characters long.");
                    return;
                }

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                admins.put(login, hashedPassword);
                System.out.println("The new administrator was successfully added.");

            } catch (ParseException e) {
                System.out.println("Failed to analyze the entered data");
            }
        } else {
            System.out.println("You can only register with another admin.");
        }
    }

    public void saveAdminsData() {
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/adminsData.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);

            objectOutputStream.writeInt(admins.size());

            for (Map.Entry<String, String> admin : admins.entrySet()) {
                objectOutputStream.writeObject(admin.getKey());
                objectOutputStream.writeObject(admin.getValue());
            }

            objectOutputStream.close();
        }catch (Exception exception) {
            System.out.println("Failed to save admin's data.");
        }
    }

    public void loadAdminsData() {
        try {
            FileInputStream fileInputStream =
                    new FileInputStream("src/main/resources/adminsData.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            admins = new HashMap<>();
            int adminsCount = objectInputStream.readInt();

            for (int i = 0; i < adminsCount; i++) {
                String key = (String) objectInputStream.readObject();
                String value = (String) objectInputStream.readObject();
                admins.put(key, value);
            }

            objectInputStream.close();
            System.out.println("Admin data uploaded successfully");
        } catch (Exception e) {
            System.out.println("Failed to load admin data.");
        }
    }
}