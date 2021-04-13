package com.nc.network.pathElements.activeElements;

import com.nc.network.pathElements.IPathElement;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Firewall extends ActiveElement {
    private static final long serialVersionUID = 5L;
    private Set<IPathElement> bannedElements;

    public Firewall() {
        bannedElements = new HashSet<>();
    }

    public Firewall(IpAddress ipAddress, int timeDelay, int costs, int maxNumOfConnections) {
        super(ipAddress, timeDelay, costs, maxNumOfConnections);
        bannedElements = new HashSet<>();
    }

    public Set<IPathElement> getBannedElements() {
        return new HashSet<>(bannedElements);
    }

    public void addBannedElement(IPathElement bannedElement) {
        bannedElements.add(bannedElement);
    }

    public void addBannedElements(List<IPathElement> bannedElements) {
        bannedElements.addAll(bannedElements);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(bannedElements.size());
        for (IPathElement pathElement : bannedElements) {
            out.writeObject(pathElement);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        int bannedElementsCount = in.readInt();

        for (int i  = 0; i < bannedElementsCount; i++) {
            addBannedElement((IPathElement)in.readObject());
        }
    }

    @Override
    public String toString() {
        return "Firewall{" + getId() + "}";
    }

    @Override
    public String getInfo() {
        return "This is a network active device that has a configurable list of banned elements. " +
                "It blocks the stream if the sender or receiver is in this list.";
    }
}
