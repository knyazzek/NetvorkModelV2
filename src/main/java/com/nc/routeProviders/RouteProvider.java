package com.nc.routeProviders;

import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.Firewall;

import java.io.*;
import java.util.*;

public abstract class RouteProvider implements IRouteProvider {
    private final Map<Integer, RoutingTableRow> routingTable;
    private Comparator<IPathElement> comparator;
    private Queue<IPathElement> availableMoves;
    private IPathElement sender;
    private boolean isSetUp;

    public RouteProvider() {
        this.routingTable = new HashMap<>();
        isSetUp = false;
    }

    public RouteProvider(Comparator<IPathElement> comparator) {
        this();
        this.comparator = comparator;
        this.availableMoves = new PriorityQueue<>(comparator);
    }

    public List<IPathElement> getRoute(Network net, IPathElement sender, IPathElement recipient)
            throws RouteNotFoundException {
        this.sender = sender;

        if (!isValidElement(this.sender) || !isValidElement(recipient)) {
            return null;
        }
        if (!isSetUp) {
            System.out.println("Configuring the routing table.");
            initRoutingTable(net);
            setUpRoutingTable(sender, recipient);
            isSetUp = true;
        }
        return getRouteByRoutingTable(recipient);
    }

    private boolean isValidElement(IPathElement element) {
        if (element == null) {
            System.out.println("Node not found.");
            return false;
        }
        if (!(element instanceof ActiveElement)) {
            System.out.println(element + " is not an Active element");
            return false;
        }

        return true;
    }

    private List<IPathElement> getRouteByRoutingTable(IPathElement recipient)
            throws RouteNotFoundException {
        List<IPathElement> route = new LinkedList<>();

        for (IPathElement pathElement = recipient; pathElement != null;
             pathElement = routingTable.get(pathElement.getId()).previous)
            route.add(pathElement);

        if (route.size() <= 1) throw new RouteNotFoundException();

        Collections.reverse(route);
        return route;
    }

    private void initRoutingTable(Network net) {
        routingTable.clear();

        for (IPathElement pathElement : net.getPathElements().values()) {
            routingTable.put(pathElement.getId(), new RoutingTableRow());
        }
        routingTable.get(sender.getId()).metric = valueOf(sender);
    }

    private void setUpRoutingTable(IPathElement sender, IPathElement recipient) {
        for (IPathElement connection : sender.getConnections()) {
            if (!routingTable.get(connection.getId()).visited) {
                int connectionMetric = routingTable.get(connection.getId()).metric;
                int connectionMetricTmp =
                        routingTable.get(sender.getId()).metric + valueOf(connection);

                if (connectionMetric == -1 ||  connectionMetricTmp < connectionMetric) {
                    routingTable.get(connection.getId()).metric = connectionMetricTmp;
                    routingTable.get(connection.getId()).setPrevious(sender);
                    availableMoves.add(connection);
                }
            }
            routingTable.get(sender.getId()).setVisitedTrue();
            availableMoves.remove(sender);
        }
        IPathElement nextStep = availableMoves.poll();

        if (nextStep instanceof Firewall) {
            Firewall firewall = (Firewall)nextStep;
            nextStep = getNextStepBasedOnBlockedElements(firewall, recipient);
        }

        if (nextStep != null) {
            setUpRoutingTable(nextStep, recipient);
        }

        ((ActiveElement)this.sender).setHasActualRouteProvider(true);
        ((ActiveElement)this.sender).setCachedRouteProvider(this);
    }

    private IPathElement getNextStepBasedOnBlockedElements(Firewall firewall, IPathElement recipient) {
        if (hasBannedSenderOrRecipient(firewall, recipient)) {
            routingTable.get(firewall.getId()).setVisitedTrue();
            routingTable.get(firewall.getId()).setPrevious(sender);
            availableMoves.remove(firewall);
            return availableMoves.poll();
        }
        return firewall;
    }

    private boolean hasBannedSenderOrRecipient(Firewall firewall, IPathElement recipient) {
        if (firewall.getBannedElements().contains(recipient) ||
            firewall.getBannedElements().contains(sender)) {
            return true;
        }
        return false;
    }

    private class RoutingTableRow implements Serializable {
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

/*        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeBoolean(isVisited());
            out.writeInt(metric);
            out.writeObject(previous);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.visited = in.readBoolean();
            this.metric = in.readInt();
            this.previous = (IPathElement) in.readObject();
        }*/

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(routingTable.size());

        for (Map.Entry<Integer, RoutingTableRow> pathElement : routingTable.entrySet()) {
            out.writeInt(pathElement.getKey());
            out.writeObject(pathElement.getValue());
        }

        out.writeObject(sender);
        out.writeBoolean(isSetUp);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int routingTableSize = in.readInt();

        for (int i =0; i < routingTableSize; i++) {
            Integer key = in.readInt();
            RoutingTableRow value = (RoutingTableRow) in.readObject();
            routingTable.put(key, value);
        }

        sender = (IPathElement) in.readObject();
        isSetUp = in.readBoolean();
    }
}
