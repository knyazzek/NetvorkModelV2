package com.nc.cli;

import com.nc.IClient;
import com.nc.INetworkTest;
import org.apache.commons.cli.*;
import java.rmi.RemoteException;

public class ClientCommandLineParser {
    private final static Options options;
    private static final CommandLineParser parser;
    private final INetworkTest networkTest;

    static {
        options = new Options();

        Option routeOption = Option.builder("r")
                .longOpt("route")
                .required(false)
                .numberOfArgs(4)
                .build();

        options.addOption(routeOption);
        parser = new DefaultParser();
    }

    public ClientCommandLineParser(INetworkTest networkTest) {
        this.networkTest = networkTest;
    }

    public void routeParse(String[] args, String loggedClientName, IClient client)
            throws ParseException {
        if (loggedClientName == null) {
            System.out.println("You must log in to calculate the route.");
            return;
        }

        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] routeArgs = commandLine.getOptionValues("route");

            String net = routeArgs[0];
            String routeProvider = routeArgs[1];
            String sender = routeArgs[2];
            String recipient = routeArgs[3];
            boolean isIp = commandLine.hasOption("ip");
            boolean isOnlyActive = commandLine.hasOption("onlyActive");

            networkTest.route(net,
                    routeProvider,
                    sender,
                    recipient,
                    isIp,
                    isOnlyActive,
                    client);
        } catch (RemoteException e) {
            System.out.println("Execution error on the server");
        }
    }
}
