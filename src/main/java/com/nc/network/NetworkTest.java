package com.nc.network;

import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.NoSuchRouteProvider;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.pathElements.IPathElement;
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

    private void loadRouteProvider(String routeProviderName) throws NoSuchRouteProvider {
        RouteProviderType rpt = RouteProviderType.getEnum(routeProviderName);

        if (rpt == null) {
            throw new NoSuchRouteProvider("The route provider with name \""
                    + routeProviderName + "\" was not found");
        }

        routeProvider = routeProviderFactory.createRouteProvider(rpt);
    }

    //TODO Remake route method
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

        if (command.size() != 5) {
            System.out.println("Invalid number of parameters entered.");
            return null;
        }

        String netName = command.get(1);
        String routeProviderName = command.get(2);
        String sender = command.get(3);
        String recipient = command.get(4);

        if (!networks.containsKey(netName)) {
            System.out.println("Network with specified name not found.");
            return null;
        }

        net = networks.get(netName);

        try {
            loadRouteProvider(routeProviderName);
        } catch (NoSuchRouteProvider noSuchRouteProvider) {
            System.out.println("Route provider with specified name not found.");
            return null;
        }

        if (isIp) {
            try {
                IpAddress senderIp = new IpAddress(sender);
                IpAddress recipientIp = new IpAddress(recipient);
                route = routeProvider.getRouteByIps(senderIp, recipientIp, net);
            } catch (InvalidIpAddressException e) {
                System.out.println("Node(s) not found.");
                return null;
            } catch (RouteNotFoundException e) {
                System.out.println("Route between two specified nodes not found.");
                return null;
            }
        } else {
            if (!NumberUtils.isCreatable(sender) ||
                    !NumberUtils.isCreatable(recipient)) {

                System.out.println("invalid node id(s) specified.");
                return null;
            }

            int senderId = Integer.parseInt(sender);
            int recipientId = Integer.parseInt(recipient);

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