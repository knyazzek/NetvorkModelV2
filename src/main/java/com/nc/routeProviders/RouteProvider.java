package com.nc.routeProviders;

import com.nc.IpAddress;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.Firewall;
import java.util.*;

public abstract class RouteProvider {
    private Map<IPathElement, RoutingTableRow> routingTable;
    private Comparator<IPathElement> comparator;
    private Queue<IPathElement> availableMoves;
    private IPathElement sender;
    private IPathElement recipient;

    public RouteProvider(Comparator<IPathElement> comparator) {
        this.routingTable = new HashMap<>();
        this.comparator = comparator;
        this.availableMoves = new PriorityQueue<>(comparator);
    }

    public List<IPathElement> getRouteByIds(int senderId, int recipientId, Network net)
            throws RouteNotFoundException {

        sender = net.getPathElementById(senderId);
        recipient = net.getPathElementById(recipientId);

        initRoutingTable(net);
        setUpRoutingTable(sender);

        List<RoutingTableRow> route = new LinkedList<>();
        return getRouteByRoutingTable();
    }

    public List<IPathElement> getRouteByIps(IpAddress senderIp, IpAddress recipientIp, Network net)
            throws RouteNotFoundException {
        return null;
    }

    private List<IPathElement> getRouteByRoutingTable() throws RouteNotFoundException {
        List<IPathElement> route = new LinkedList<>();

        for (IPathElement pathElement = recipient; pathElement != null;
             pathElement = routingTable.get(pathElement).previous)
            route.add(pathElement);

        if (route.size() <= 1) throw new RouteNotFoundException();

        Collections.reverse(route);
        return route;
    }

    private void initRoutingTable(Network net) {
        routingTable.clear();

        for (IPathElement pathElement : net.getPathElements().values()) {
            routingTable.put(pathElement, new RoutingTableRow());
        }
        routingTable.get(sender).metric = valueOf(sender);
    }

    private void setUpRoutingTable(IPathElement sender) {
        for (IPathElement connection : sender.getConnections()) {
            if (!routingTable.get(connection).visited) {
                int connectionMetric = routingTable.get(connection).metric;
                int connectionMetricTmp =
                        routingTable.get(sender).metric + valueOf(connection);

                if (connectionMetric == -1 ||  connectionMetricTmp < connectionMetric) {
                    routingTable.get(connection).metric = connectionMetricTmp;
                    routingTable.get(connection).setPrevious(sender);
                    availableMoves.add(connection);
                }
            }
            routingTable.get(sender).setVisitedTrue();
            availableMoves.remove(sender);
        }
        IPathElement nextStep = availableMoves.poll();

        if (nextStep != null && nextStep instanceof Firewall) {
            if (((Firewall) nextStep).getBannedElements().contains(recipient)) {
                routingTable.get(nextStep).setVisitedTrue();
                routingTable.get(nextStep).setPrevious(sender);
                availableMoves.remove(nextStep);
                nextStep = availableMoves.poll();
            }
        }

        if (nextStep != null)
            setUpRoutingTable(nextStep);
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

        public IPathElement getPrevious() {
            return previous;
        }

        public void setPrevious(IPathElement previous) {
            this.previous = previous;
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

    public abstract int valueOf(IPathElement pathElement);
}
