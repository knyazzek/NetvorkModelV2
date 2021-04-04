package com.nc;

import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.NoPortsAvailableException;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.activeElements.Firewall;
import com.nc.network.pathElements.activeElements.PC;
import com.nc.network.pathElements.passiveElements.Cable;
import com.nc.routeProviders.MinNodesCountRouteProvider;
import com.nc.routeProviders.MinTimeDelayRouteProvider;
import com.nc.routeProviders.RouteProvider;

public class Main {
    public static void main(String[] args) {
        Network network = new Network("MainNetwork");

        try {
            IpAddress pc1Ip = new IpAddress(new int[]{1,1,1,1});
            IpAddress pc2Ip = new IpAddress(new int[]{2,2,2,2});
            IpAddress pc3Ip = new IpAddress(new int[]{3,3,3,3});
            IpAddress pc4Ip = new IpAddress(new int[]{4,4,4,4});
            IpAddress pc5Ip = new IpAddress(new int[]{5,5,5,5});

            PC pc1 = new PC(0,pc1Ip, 3,1,4);
            Firewall firewall = new Firewall(1,pc2Ip,2,2,4);
            PC pc3 = new PC(2,pc3Ip,5,3,4);
            PC pc4 = new PC(3,pc4Ip,6,3,4);
            PC pc5 = new PC(4,pc5Ip,4,3,4);

            firewall.addBannedElement(pc4);

            Cable cable1 = new Cable(5,2,1, pc1, firewall);
            Cable cable2 = new Cable(6,3,1, pc1, pc3);
            Cable cable3 = new Cable(7,6,1, pc3, pc4);
            Cable cable4 = new Cable(8,6,1, firewall, pc4);
            Cable cable5 = new Cable(9,3,1, firewall, pc5);
            Cable cable6 = new Cable(10,3,1, pc3, pc5);

            network.addPathElement(pc1);
            network.addPathElement(firewall);
            network.addPathElement(pc3);
            network.addPathElement(pc4);
            network.addPathElement(pc5);

            network.addPathElement(cable1);
            network.addPathElement(cable2);
            network.addPathElement(cable3);
            network.addPathElement(cable4);
            network.addPathElement(cable5);
            network.addPathElement(cable6);
        } catch (InvalidIpAddressException exception) {
            System.out.println("One of the IP has an incorrect format.");
        } catch (NoPortsAvailableException exception) {
            System.out.println("One of the elements doesn't have available port.");
        }

        RouteProvider rp = MinNodesCountRouteProvider.getInstance();
        try {
            System.out.println(rp.getRouteByIds(0,3,network));
        } catch (RouteNotFoundException e) {
            System.out.println("Route not found.");
        }
    }
}