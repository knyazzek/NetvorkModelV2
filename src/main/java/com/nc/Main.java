package com.nc;

import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.NoPortsAvailableException;
import com.nc.network.Network;
import com.nc.network.NetworkTest;
import com.nc.network.pathElements.activeElements.Firewall;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.activeElements.PC;
import com.nc.network.pathElements.passiveElements.Cable;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Main {
    public static void main(String[] args) {
        NetworkTest networkTest = new NetworkTest();
        networkTest.start();
    }
}