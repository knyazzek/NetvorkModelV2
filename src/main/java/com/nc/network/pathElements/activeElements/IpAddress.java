package com.nc.network.pathElements.activeElements;

import com.nc.exceptions.InvalidIpAddressException;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

public class IpAddress implements Externalizable {
    private static final long serialVersionUID = 9L;
    private int[] nodeAddress;

    public IpAddress() {}

    public IpAddress(int[] nodeAddress) throws InvalidIpAddressException {
        setNodeAddress(nodeAddress);
    }

    public IpAddress(String nodeAddressString) throws InvalidIpAddressException {
        String[] octets = nodeAddressString.split("\\.");
        if (octets.length != 4) throw new InvalidIpAddressException("The IP address must have 4 octets.");

        int[] nodeAddress = new int[4];

        for (int i = 0; i < octets.length; i++) {
            if (NumberUtils.isCreatable(octets[i]))
                nodeAddress[i] = Integer.parseInt(octets[i]);
            else
                throw new InvalidIpAddressException("The octet of the Ip address must contain only digits.");

            setNodeAddress(nodeAddress);
        }
    }

    public IpAddress(IpAddress ipAddress) {
        nodeAddress = ipAddress.getNodeAddress();
    }

    public int[] getNodeAddress() {
        return Arrays.copyOf(nodeAddress, nodeAddress.length);
    }

    public void setNodeAddress(int[] nodeAddress) throws InvalidIpAddressException{
        if (nodeAddress.length != 4) throw
                new InvalidIpAddressException("The IP address must have 4 octets.");

        for (int octet : nodeAddress) {
            if (octet < 0 || octet > 255) throw
                    new InvalidIpAddressException("The octet value must be in the range from 0 to 255.");
        }

        this.nodeAddress = Arrays.copyOf(nodeAddress, nodeAddress.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress = (IpAddress) o;
        return Arrays.equals(nodeAddress, ipAddress.nodeAddress);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nodeAddress);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(nodeAddress.length);
        for (int i = 0; i < nodeAddress.length; i++) {
            out.writeInt(nodeAddress[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        int arrayLength = in.readInt();
        int[] nodeAddressTmp = new int[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            nodeAddressTmp[i] = in.readInt();
        }
        nodeAddress = nodeAddressTmp;
    }

    @Override
    public String toString() {
        return "IpAddress{" + Arrays.toString(nodeAddress) +
                '}';
    }
}
