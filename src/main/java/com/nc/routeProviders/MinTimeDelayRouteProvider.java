package com.nc.routeProviders;

import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import java.util.*;

public class MinTimeDelayRouteProvider implements RouteProvider{
    Map<IPathElement, RoutingTableRow> routingTable;
    Comparator<IPathElement> comparator;
    Queue<IPathElement> availableMoves;

    public MinTimeDelayRouteProvider() {
        this.routingTable = new HashMap<>();
        comparator = (o1, o2) -> o1.getTimeDelay() - o2.getTimeDelay();
        this.availableMoves = new PriorityQueue<>(comparator);
    }

    @Override
    public List<IPathElement> getRouteByIds(int senderId, int recipientId, Network net)
            throws RouteNotFoundException {

        IPathElement sender = net.getPathElementById(senderId);
        IPathElement recipient = net.getPathElementById(recipientId);

        initRoutingTable(net, sender);
        setUpRoutingTable(sender, net);

        List<RoutingTableRow> route = new LinkedList<>();
        return getRouteByRoutingTable(recipient);
    }

    private List<IPathElement> getRouteByRoutingTable(IPathElement recipient) throws RouteNotFoundException {
        List<IPathElement> route = new LinkedList<>();

        for (IPathElement pathElement = recipient; pathElement != null;
             pathElement = routingTable.get(pathElement).previous)
        route.add(pathElement);

        if (route.size() <= 1) throw new RouteNotFoundException();

        Collections.reverse(route);
        System.out.println(routingTable);
        return route;
    }

    @Override
    public List<IPathElement> getRouteByIps(int senderIp, int recipientIp, Network net)
            throws RouteNotFoundException {
        return null;
    }

    public void initRoutingTable(Network net, IPathElement sender) {
        routingTable.clear();

        for (IPathElement pathElement : net.getPathElements().values()) {
            routingTable.put(pathElement, new RoutingTableRow());
        }

        routingTable.get(sender).metric = sender.getTimeDelay();
    }

    private void setUpRoutingTable(IPathElement sender, Network net) {
        for (IPathElement connection : sender.getConnections()) {
            if (!routingTable.get(connection).visited) {
                int connectionMetric = routingTable.get(connection).metric;
                int connectionMetricTmp =
                        routingTable.get(sender).metric + connection.getTimeDelay();

                if (connectionMetric == -1 ||  connectionMetricTmp < connectionMetric) {
                    routingTable.get(connection).metric = connectionMetricTmp;
                    routingTable.get(connection).previous = sender;
                    availableMoves.add(connection);
                }
            }

            routingTable.get(sender).setVisitedTrue();
            availableMoves.remove(sender);
        }

        IPathElement nextStep = availableMoves.poll();

        if (nextStep != null) {
            setUpRoutingTable(nextStep, net);
        }
    }

    private class RoutingTableRow {
        private boolean visited;
        private int metric;
        private IPathElement previous;

        public RoutingTableRow() {
            this.visited = false;
            this.metric = -1;
            previous = null;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisitedTrue() {
            this.visited = true;
        }

        public int getMetric() {
            return metric;
        }

        public void setMetric(int metric) {
            this.metric = metric;
        }

        public void changeRoutingTableRow(int metric, IPathElement previous) {
            this.metric = metric;
            this.previous = previous;
        }

        @Override
        public String toString() {
            return "RoutingTableRow{" +
                    "visited=" + visited +
                    ", metric=" + metric +
                    ", previous=" + previous +
                    '}';
        }
    }
}
