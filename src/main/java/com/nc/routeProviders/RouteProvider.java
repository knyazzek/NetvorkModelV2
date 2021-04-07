package com.nc.routeProviders;

import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.network.Network;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.Firewall;
import com.nc.network.pathElements.activeElements.PC;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public abstract class RouteProvider implements IRouteProvider {
    private Map<IPathElement, RoutingTableRow> routingTable;
    private Comparator<IPathElement> comparator;
    private Queue<IPathElement> availableMoves;
    private IPathElement sender;
    private IPathElement recipient;
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
        this.recipient = recipient;

        if (!isValidSenderAndRecipient()) {
            return null;
        }
        if (!isSetUp) {
            initRoutingTable(net);
            setUpRoutingTable(sender);
        }
        return getRouteByRoutingTable();
    }

    private boolean isValidSenderAndRecipient() {
        if (sender == null || recipient == null) {
            System.out.println("Node(s) not found.");
            return false;
        }
        if (!(sender instanceof PC) || !(recipient instanceof PC)) {
            System.out.println("You can only find the route from PC to PC");
            return false;
        }

        return true;
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

        if (nextStep instanceof Firewall) {
            Firewall firewall = (Firewall)nextStep;
            nextStep = getNextStepBasedOnBlockedElements(firewall);
        }

        if (nextStep != null) {
            setUpRoutingTable(nextStep);
        }

        ((ActiveElement)this.sender).setHasActualRouteProvider(true);
        ((ActiveElement)this.sender).setCachedRouteProvider(this);
    }

    private IPathElement getNextStepBasedOnBlockedElements(Firewall firewall) {
        if (hasBannedSenderOrRecipient(firewall)) {
            routingTable.get(firewall).setVisitedTrue();
            routingTable.get(firewall).setPrevious(sender);
            availableMoves.remove(firewall);
            return availableMoves.poll();
        }
        return firewall;
    }

    private boolean hasBannedSenderOrRecipient(Firewall firewall) {
        if (firewall.getBannedElements().contains(recipient) ||
            firewall.getBannedElements().contains(sender)) {
            return true;
        }
        return false;
    }

    private class RoutingTableRow implements Externalizable {
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

    public boolean isSetUp() {
        return isSetUp;
    }

    public IPathElement getRecipient() {
        return recipient;
    }

    public void setRecipient(IPathElement recipient) {
        this.recipient = recipient;
    }

    public void setSetUp(boolean setUp) {
        isSetUp = setUp;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(routingTable.size());

        for (Map.Entry<IPathElement, RoutingTableRow> pathElement : routingTable.entrySet()) {
            out.writeObject(pathElement.getKey());
            out.writeObject(pathElement.getValue());
        }

        out.writeObject(sender);
        out.writeObject(recipient);
        out.writeBoolean(isSetUp);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int routingTableSize = in.readInt();

        for (int i =0; i < routingTableSize; i++) {
            IPathElement key = (IPathElement) in.readObject();
            RoutingTableRow value = (RoutingTableRow) in.readObject();
            routingTable.put(key, value);
        }

        sender = (IPathElement) in.readObject();
        recipient = (IPathElement) in.readObject();
        isSetUp = in.readBoolean();
    }
}
