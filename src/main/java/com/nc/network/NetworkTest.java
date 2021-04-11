package com.nc.network;

import com.nc.exceptions.*;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.passiveElements.PassiveElement;
import com.nc.routeProviders.IRouteProvider;
import com.nc.routeProviders.RouteProviderFactory;
import com.nc.routeProviders.RouteProviderType;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class NetworkTest {
    private Network network;
    private RouteProviderFactory routeProviderFactory;
    private IRouteProvider routeProvider;

    public NetworkTest() {
        routeProviderFactory = new RouteProviderFactory();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Input:");
            String str = scanner.nextLine();
            String[] commandLine = str.split(" ");
            CommandType commandType = CommandType.fromString(commandLine[0]);

            if (commandType == null) {
                System.out.println(commandLine[0] + " is not recognized as a command.");
                continue;
            }

            switch (commandType) {
                case ROUTE :
                    List<IPathElement> res = route(commandLine);
                    if (res != null)
                        System.out.println(res);
                    break;

                case EXIT :
                    System.out.println("Exit.");
                    System.exit(0);
                    break;
            }
        }
    }

    private void loadNetwork(String networkName) throws IOException, ClassNotFoundException {

        if (network != null && network.getName().equals(networkName)) {
            return;
        }

        FileInputStream fileInputStream =
                new FileInputStream("src/main/resources/" + networkName + ".ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        network = (Network) objectInputStream.readObject();

        if (network == null) {
            throw  new ClassNotFoundException();
        }

        objectInputStream.close();
    }

    //No one should change the network at this time
    private List<IPathElement> route(String[] commands) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();

        String netName;
        String routeProviderName;
        String senderParameter;
        String recipientParameter;
        boolean isIp = false;
        boolean isOnlyActive = false;

        ActiveElement sender;
        ActiveElement recipient;

        try {
            CommandLine commandLine = parser.parse(options, commands);

            if (commandLine.hasOption("ip")) {
                isIp = true;
            }

            if (commandLine.hasOption("a")) {
                isOnlyActive = true;
            }

            String[] routeArgs = commandLine.getOptionValues("route");

            netName = routeArgs[0];
            routeProviderName = routeArgs[1];
            senderParameter = routeArgs[2];
            recipientParameter = routeArgs[3];

        } catch (ParseException e) {
            System.out.println("Invalid flag(s) specified.");
            return null;
        }

        //Load Network
        try {
            loadNetwork(netName);
        } catch (Exception e) {
            System.out.println("Failed to load network with specified name.");
            return null;
        }

        //Load Sender and Recipient
        sender = getActiveElement(isIp, senderParameter, network);
        recipient = getActiveElement(isIp, recipientParameter, network);

        if (sender == null || recipient == null) {
            return null;
        }

        //LoadRouteProvider
        try {
            loadRouteProvider(routeProviderName, sender);
        } catch (NoSuchRouteProvider noSuchRouteProvider) {
            System.out.println("Route provider with specified name not found.");
            return null;
        }

        return getRoute(network, sender, recipient, isOnlyActive);
    }

    private Options getOptions() {
        Options options = new Options();

        Option routeOption = Option.builder("r")
                .longOpt("route")
                .required(false)
                .numberOfArgs(4)
                .build();

        Option ipOption = Option.builder("i")
                .longOpt("ip")
                .required(false)
                .hasArg(false)
                .build();

        Option onlyActiveOption = Option.builder("a")
                .longOpt("onlyActive")
                .required(false)
                .hasArg(false)
                .build();

        options.addOption(routeOption)
                .addOption(ipOption)
                .addOption(onlyActiveOption);

        return options;
    }

    private ActiveElement getActiveElement(boolean isIp, String pathElementParameter, Network net) {
        ActiveElement activeElement;

        if (isIp) {
            try {
                activeElement = net.getPathElementByIp(new IpAddress(pathElementParameter));
            } catch (InvalidIpAddressException e) {
                System.out.println(pathElementParameter + " is invalid Ip Address");
                return null;
            }
        } else {
            if (!NumberUtils.isCreatable(pathElementParameter)){
                System.out.println(pathElementParameter + " is invalid Id");
                return null;
            }
            int pathElementId = Integer.parseInt(pathElementParameter);
            activeElement = net.getPathElementById(pathElementId);
        }

        if (activeElement == null)
            System.out.println("The element with specified parameters \"" + pathElementParameter
                    + "\" doesn't exist or is not an active element.");

        return activeElement;
    }

    private void loadRouteProvider(String routeProviderName, ActiveElement sender)
            throws NoSuchRouteProvider {

        if (sender.hasActualRouteProvider() &&
                sender.getCachedRouteProviderName().equals(routeProviderName)) {
            System.out.println("We use a cached routing table.");
            routeProvider = sender.getCachedRouteProvider();
        } else {
            RouteProviderType rpt = RouteProviderType.fromString(routeProviderName);

            if (rpt == null) {
                throw new NoSuchRouteProvider("The route provider with name \""
                        + routeProviderName + "\" was not found");
            }
            routeProvider = routeProviderFactory.createRouteProvider(rpt);
        }
    }

    private List<IPathElement> getRoute(Network net,
                                        IPathElement sender,
                                        IPathElement recipient,
                                        boolean isOnlyActive) {
        List<IPathElement> route;

        try {
            route = routeProvider.getRoute(net, sender, recipient);
        } catch (RouteNotFoundException e) {
            System.out.println("The route between the two nodes was not found");
            return null;
        }

        if (isOnlyActive) {
            for (int i = 0; i < route.size(); i++) {
                if (route.get(i) instanceof PassiveElement)
                    route.remove(route.get(i));
            }
        }
        return route;
    }
}