package com.nc;

import com.nc.exceptions.InvalidIpAddressException;
import java.util.Arrays;

public class IpAddress {
    private int[] nodeAddress;

    public IpAddress(int[] nodeAddress) throws InvalidIpAddressException {
        if (nodeAddress.length != 4) throw
                new InvalidIpAddressException("The IP address must have 4 octets.");

        for (int octet : nodeAddress) {
            if (octet < 0 || octet > 255) throw
                    new InvalidIpAddressException("The octet value must be in the range from 0 to 255.");
        }

        this.nodeAddress = Arrays.copyOf(nodeAddress, nodeAddress.length);
    }

    public IpAddress(IpAddress ipAddress) {
        nodeAddress = ipAddress.getNodeAddress();
    }

    public int[] getNodeAddress() {
        return Arrays.copyOf(nodeAddress, nodeAddress.length);
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
}
