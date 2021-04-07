package com.nc.network;

import com.nc.exceptions.*;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.passiveElements.PassiveElement;
import com.nc.routeProviders.IRouteProvider;
import com.nc.routeProviders.RouteProvider;
import com.nc.routeProviders.RouteProviderFactory;
import com.nc.routeProviders.RouteProviderType;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class NetworkTest {
    private Map<String, Network> networks;
    private RouteProviderFactory routeProviderFactory;
    private IRouteProvider routeProvider;

    public NetworkTest() {
        networks = new HashMap<>();
        routeProviderFactory = new RouteProviderFactory();
    }

    public void start() {
        try {
            loadNetworks();
        } catch (Exception e) {
            System.out.println("Failed to load networks.");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Input:");
            String str = scanner.nextLine();
            String[] command = str.split(" ");

            switch (command[0]) {
                case ("route") :
                    List<IPathElement> res = route(Arrays.asList(command));
                    if (res != null)
                        System.out.println(res);
                    break;

                case ("exit") :
                    System.out.println("Exit.");
                    System.exit(0);
                    break;

                default:
                    System.out.println(command[0] +
                            " is not recognized as a command.");
                    break;
            }
        }
    }

    private void loadNetworks() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/save.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        Network network = (Network) objectInputStream.readObject();
        networks.put(network.getName(), network);
        objectInputStream.close();
    }

    private List<IPathElement> route(List<String> command) {
        List<String> commandTmp = new LinkedList<>(command);
        boolean isIp = false;
        boolean isOnlyActive = false;
        Network net;
        ActiveElement sender;
        ActiveElement recipient;

        if (commandTmp.contains("-ip")) {
            isIp = true;
            commandTmp.remove("-ip");
        }

        if (commandTmp.contains("-a")) {
            isOnlyActive = true;
            commandTmp.remove("-a");
        }

        if (commandTmp.size() != 5) {
            System.out.println("Invalid number of parameters entered.");
            System.out.println(commandTmp);
            return null;
        }

        String netName = commandTmp.get(1);
        String routeProviderName = commandTmp.get(2);
        String senderParameter = commandTmp.get(3);
        String recipientParameter = commandTmp.get(4);

        //Load Network
        if (!networks.containsKey(netName)) {
            System.out.println("Network with specified name not found.");
            return null;
        }
        net = networks.get(netName);

        //Load Sender and Recipient
        sender = getActiveElement(isIp, senderParameter, net);
        recipient = getActiveElement(isIp, recipientParameter, net);

        if (sender == null || recipient == null) {
            return null;
        }

        //LoadRouteProvider
        try {
            loadRouteProvider(routeProviderName, sender, recipient);
        } catch (NoSuchRouteProvider noSuchRouteProvider) {
            System.out.println("Route provider with specified name not found.");
            return null;
        }

        return getRoute(isOnlyActive, net, sender, recipient);
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

    private void loadRouteProvider(String routeProviderName, ActiveElement sender, ActiveElement recipient)
            throws NoSuchRouteProvider {
        if (sender.hasActualRouteProvider()) {
            RouteProvider routeProviderTmp = sender.getCachedRouteProvider();

            if (!routeProviderTmp.getRecipient().equals(recipient)) {
                routeProviderTmp.setRecipient(recipient);
            }

            routeProvider = sender.getCachedRouteProvider();
        } else {
            RouteProviderType rpt = RouteProviderType.getEnum(routeProviderName);

            if (rpt == null) {
                throw new NoSuchRouteProvider("The route provider with name \""
                        + routeProviderName + "\" was not found");
            }
            routeProvider = routeProviderFactory.createRouteProvider(rpt);
        }
    }

    private List<IPathElement> getRoute(boolean isOnlyActive,
                                        Network net,
                                        IPathElement sender,
                                        IPathElement recipient) {
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