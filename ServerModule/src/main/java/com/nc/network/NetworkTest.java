package com.nc.network;

import com.nc.INetworkTest;
import com.nc.routeProviders.IRouteProvider;
import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.NoSuchRouteProvider;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.network.pathElements.activeElements.Firewall;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.passiveElements.PassiveElement;
import com.nc.routeProviders.RouteProviderFactory;
import com.nc.routeProviders.RouteProviderType;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class NetworkTest implements INetworkTest {
    private Map<String, Network> networks;
    private final RouteProviderFactory routeProviderFactory;

    public NetworkTest() {
        routeProviderFactory = new RouteProviderFactory();
        networks = new HashMap<>();
    }

    private void loadNetwork(String networkName) throws IOException, ClassNotFoundException {
        if (networks.containsKey(networkName)) {
            return;
        }

        FileInputStream fileInputStream =
                new FileInputStream("src/main/resources/" + networkName + ".ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Network network = (Network) objectInputStream.readObject();

        if (network == null) {
            throw  new ClassNotFoundException();
        }

        networks.put(networkName, network);
        objectInputStream.close();
    }

    public synchronized List<String> route(String[] commands) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();

        String netName;
        String routeProviderName;
        String senderParameter;
        String recipientParameter;
        boolean isIp = false;
        boolean isOnlyActive = false;

        Network net;
        IRouteProvider routeProvider;
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
            net = networks.get(netName);
        } catch (Exception e) {
            System.out.println("Failed to load network with specified name.");
            return null;
        }

        //Load Sender and Recipient
        sender = getActiveElement(isIp, senderParameter, net);
        recipient = getActiveElement(isIp, recipientParameter, net);

        if (sender == null || recipient == null) {
            return null;
        }

        //LoadRouteProvider
        try {
            routeProvider = getRouteProvider(routeProviderName, sender);
        } catch (NoSuchRouteProvider noSuchRouteProvider) {
            System.out.println("Route provider with specified name not found.");
            return null;
        }

        List<IPathElement> route =
                getRoute(net, routeProvider, sender, recipient, isOnlyActive);

        if (route == null) {
            return null;
        }

        List<String> res = new ArrayList<>();

        for (IPathElement pathElement : route) {
            res.add(pathElement.toString());
        }
        return res;
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

    private IRouteProvider getRouteProvider(String routeProviderName, ActiveElement sender)
            throws NoSuchRouteProvider {

        if (sender.hasActualRouteProvider() &&
                sender.getCachedRouteProviderName().equals(routeProviderName)) {
            System.out.println("We use a cached routing table.");
            return sender.getCachedRouteProvider();
        } else {
            RouteProviderType rpt = RouteProviderType.fromString(routeProviderName);

            if (rpt == null) {
                throw new NoSuchRouteProvider("The route provider with name \""
                        + routeProviderName + "\" was not found");
            }
            return routeProviderFactory.createRouteProvider(rpt);
        }
    }

    private List<IPathElement> getRoute(Network net,
                                        IRouteProvider routeProvider,
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

    public void changeBannedList(String netName,
                                 Integer firewallId,
                                 Integer bannedElementId,
                                 boolean isDelete) {
        try {
            loadNetwork(netName);
        } catch (Exception e) {
            System.out.println("Changing failed.");
        }

        Network net = networks.get(netName);
        ActiveElement activeElement = net.getPathElementById(firewallId);
        IPathElement bannedElement = net.getPathElementById(bannedElementId);

        if (activeElement instanceof Firewall) {
            Firewall firewall = (Firewall) activeElement;

            if (isDelete) {
                firewall.removeBannedElement(bannedElement);
            } else {
                firewall.addBannedElement(bannedElement);
            }
        }
        System.out.println("Changing successful");
    }

    public void refreshAllCachedRouteProvidersOf(String netName) {
        networks.get(netName).refreshAllCachedRouteProviders();
    }
}