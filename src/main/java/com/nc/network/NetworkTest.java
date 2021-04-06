package com.nc.network;

import com.nc.exceptions.ElementNotFoundException;
import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.NoSuchRouteProvider;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.passiveElements.PassiveElement;
import com.nc.routeProviders.IRouteProvider;
import com.nc.routeProviders.RouteProvider;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class NetworkTest {
    private Map<String, Network> networks;
    private IRouteProvider routeProvider;

    public NetworkTest() {
        networks = new HashMap<>();
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

    private void loadRouteProvider(String routeProviderName) throws NoSuchRouteProvider {
        try {
            Class routeProviderClass = Class.forName("com.nc.routeProviders." + routeProviderName);
            routeProvider = (RouteProvider) routeProviderClass.newInstance();
        } catch (Exception e) {
            throw new NoSuchRouteProvider(e);
        }
    }

    private List<IPathElement> route(List<String> command) {
        List<String> commandTmp = new LinkedList<>(command);
        boolean isIp = false;
        boolean isOnlyActive = false;
        Network net;
        List<IPathElement> route;

        if (commandTmp.contains("-ip")) {
            isIp = true;
            commandTmp.remove("-ip");
        }

        if (commandTmp.contains("-a")) {
            isOnlyActive = true;
            commandTmp.remove("-a");
        }

        if (!networks.containsKey(commandTmp.get(1))) {
            System.out.println("Network with specified name not found.");
            return null;
        }

        net = networks.get(commandTmp.get(1));

        try {
            loadRouteProvider(commandTmp.get(2));
        } catch (NoSuchRouteProvider noSuchRouteProvider) {
            System.out.println("Route provider with specified name not found.");
            return null;
        }

        if (isIp) {
            try {
                IpAddress senderIp = new IpAddress(commandTmp.get(3));
                IpAddress recipientIp = new IpAddress(commandTmp.get(4));
                route = routeProvider.getRouteByIps(senderIp, recipientIp, net);
            } catch (InvalidIpAddressException | ElementNotFoundException e) {
                System.out.println("Node(s) not found.");
                return null;
            } catch (RouteNotFoundException e) {
                System.out.println("Route between two specified nodes not found.");
                return null;
            }
        } else {
            if (!NumberUtils.isCreatable(commandTmp.get(3)) ||
                    !NumberUtils.isCreatable(commandTmp.get(4))) {

                System.out.println("invalid node id(s) specified.");
                return null;
            }

            int senderId = Integer.parseInt(commandTmp.get(3));
            int recipientId = Integer.parseInt(commandTmp.get(4));

            try {
                route = routeProvider.getRouteByIds(senderId, recipientId, net);
            } catch (RouteNotFoundException e) {
                System.out.println("Route between two specified nodes not found");
                return null;
            }
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