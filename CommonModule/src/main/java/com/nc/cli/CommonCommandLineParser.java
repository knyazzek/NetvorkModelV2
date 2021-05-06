package com.nc.cli;

import com.nc.INetworkTest;
import com.nc.exceptions.login.IncorrectLoginDataException;
import com.nc.exceptions.login.LoginIsAlreadyInUseException;
import com.nc.exceptions.logout.LogoutFromNotLoggedInUserException;
import com.nc.exceptions.registration.LoginIsAlreadyExistException;
import org.apache.commons.cli.*;
import java.rmi.RemoteException;

public class CommonCommandLineParser {
    private final static Options options;
    private static final CommandLineParser parser;
    private final INetworkTest networkTest;

    static {
        options = new Options();

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

        options.addOption(loginOption)
                .addOption(registrationOption);

        parser = new DefaultParser();
    }

    public CommonCommandLineParser(INetworkTest networkTest) {
        this.networkTest = networkTest;
    }

    public String loginParse(String[] args)
            throws ParseException, RemoteException {
        if (args.length != 3) {
            System.out.println("Invalid number of parameters.");
            return null;
        }
        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] loginArgs = commandLine.getOptionValues("login");
            String login = loginArgs[0];
            String password = loginArgs[1];

            String loggedClientName = networkTest.login(login, password);

            if (loggedClientName == null) {
                System.out.println("Login failed.");
            } else {
                System.out.println("Login completed.");
            }
            return loggedClientName;
        } catch (LoginIsAlreadyInUseException e) {
            System.out.println("Specified login is already in use.");
        } catch (IncorrectLoginDataException e) {
            System.out.println("Incorrect login or password entered");
        }
        return null;
    }

    public void logoutParse(String[] args, String loggedClientName)
            throws RemoteException {
        if (args.length != 1) {
            System.out.println("Invalid number of parameters.");
            return;
        }
        try {
            networkTest.logout(loggedClientName);
            System.out.println("Logout completed.");
        } catch (LogoutFromNotLoggedInUserException e) {
            System.out.println("You can't logout from not logged in user");
        }
    }

    public void registrationParse(String[] args)
            throws RemoteException, ParseException{
        if (args.length != 3) {
            System.out.println("Invalid number of parameters.");
            return;
        }
        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] registrationArgs = commandLine.getOptionValues("registration");
            String login = registrationArgs[0];
            String password = registrationArgs[1];

            networkTest.registration(login, password);
            System.out.println("Registration completed.");
        } catch (LoginIsAlreadyExistException e) {
            System.out.println("Specified login is already exist.");
        }
    }
}
