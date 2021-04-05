package com.nc.network;

import com.nc.exceptions.ElementNotFoundException;
import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.NoSuchRouteProvider;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.routeProviders.RouteProvider;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetworkTest {
    private List<Network> networkList;
    private RouteProvider routeProvider;

    public NetworkTest() {
        networkList = new ArrayList<>();
    }

    public void start() {
        try {
            loadNetworks();
        } catch (Exception e) {
            System.out.println("Failed to load networks");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        outer:
        while (true) {
            System.out.print("Input:");
            String str = scanner.nextLine();
            String[] command = str.split(" ");

            switch (command[0]) {
                case ("route") :
                    try {
                        System.out.println(route(command));
                    } catch (Exception e) {
                        continue;
                    }
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
        networkList.add(network);
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

    private List<IPathElement> route(String[] command) throws
            NoSuchRouteProvider, RouteNotFoundException, ElementNotFoundException {
        String networkName;
        String routerProviderName;
        Network network = null;

        switch (command.length) {
            case (5) :
                if (!NumberUtils.isCreatable(command[3]) || !NumberUtils.isCreatable(command[4])) {
                    System.out.println("The Id must be a number");
                    return null;
                }

                int senderId = Integer.parseInt(command[3]);
                int recipientId = Integer.parseInt(command[4]);

                networkName = command[1];
                routerProviderName = command[2];

                for (Network net: networkList) {
                    if (net.getName().equals(networkName))
                        network = net;
                }

                if (network != null) {
                    loadRouteProvider(routerProviderName);
                    return (routeProvider.getRouteByIds(senderId, recipientId, network));
                } else
                    System.out.println("Network not found");

                break;

            case(6) :
                if (command[1].equals("-ip")) {
                    try {
                        IpAddress senderIp = new IpAddress(command[4]);
                        IpAddress recipientIp = new IpAddress(command[5]);

                        networkName = command[2];
                        routerProviderName = command[3];

                        for (Network net : networkList) {
                            if (net.getName().equals(networkName))
                                network = net;
                        }

                        if (network != null) {
                            loadRouteProvider(routerProviderName);
                            return (routeProvider.getRouteByIps(senderIp, recipientIp, network));
                        } else
                            System.out.println("Network not found");

                    } catch (InvalidIpAddressException e) {
                        System.out.println("Invalid Ip address specified.");
                        return null;
                    }
                }
                break;
        }
        return null;
    }
}