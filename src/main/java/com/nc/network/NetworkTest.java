package com.nc.network;

import com.nc.exceptions.*;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.passiveElements.PassiveElement;
import com.nc.routeProviders.IRouteProvider;
import com.nc.routeProviders.RouteProviderFactory;
import com.nc.routeProviders.RouteProviderType;
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

    private void loadNetwork(String networkName) throws IOException, ClassNotFoundException {

        if (network != null && network.getName().equals(networkName)) {
            return;
        }

        FileInputStream fileInputStream = new FileInputStream("src/main/resources/save.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        Map<String, Network> networks = new HashMap<>();
        int networksCount = objectInputStream.readInt();

        for (int i = 0; i < networksCount; i++) {
            String key = (String) objectInputStream.readObject();
            Network value = (Network) objectInputStream.readObject();

            networks.put(key, value);
        }

        network = networks.get(networkName);

        if (network == null) {
            throw  new ClassNotFoundException();
        }

        objectInputStream.close();
    }

    //No one should change the network at this time
    synchronized private List<IPathElement> route(List<String> command) {
        List<String> commandTmp = new LinkedList<>(command);
        boolean isIp = false;
        boolean isOnlyActive = false;

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
            return null;
        }

        String netName = commandTmp.get(1);
        String routeProviderName = commandTmp.get(2);
        String senderParameter = commandTmp.get(3);
        String recipientParameter = commandTmp.get(4);

        //Load Network
        try {
            loadNetwork(netName);
        } catch (Exception e) {
            System.out.println("Failed to load network with specified name.");
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
                //TODO rewrite
                sender.getCachedRouteProvider().getClass().getSimpleName().equals(routeProviderName)) {
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