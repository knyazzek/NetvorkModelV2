package com.nc.cli;

import com.nc.INetworkTest;
import org.apache.commons.cli.*;
import java.rmi.RemoteException;

public class ServerCommandLineParser {
    private final static Options options;
    private static final CommandLineParser parser;
    private final INetworkTest networkTest;

    static {
        options = new Options();

        Option configFirewallOption = Option.builder("f")
                .longOpt("fconfig")
                .required(false)
                .numberOfArgs(3)
                .build();

        Option deleteBannedElementOption = Option.builder("d")
                .longOpt("del")
                .required(false)
                .hasArg(false)
                .build();

        options.addOption(configFirewallOption)
                .addOption(deleteBannedElementOption);

        parser = new DefaultParser();
    }

    public ServerCommandLineParser(INetworkTest networkTest) {
        this.networkTest = networkTest;
    }

    public void firewallConfigParse(String[] args, String loggedAdminName) {
        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] configFirewallArgs = commandLine.getOptionValues("fconfig");

            String netName = configFirewallArgs[0];
            String firewallIdStr = configFirewallArgs[1];
            String bannedElementIdStr = configFirewallArgs[2];
            boolean isDelete = commandLine.hasOption("del");

            networkTest.configFirewall(netName, firewallIdStr,
                    bannedElementIdStr, loggedAdminName, isDelete);
        } catch (ParseException | RemoteException e) {
            System.out.println("Incorrect data for command \"" + args[0] + "\".");
        }
    }
}