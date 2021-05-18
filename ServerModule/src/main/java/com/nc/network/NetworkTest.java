package com.nc.network;

import com.nc.IClient;
import com.nc.INetworkTest;
import com.nc.exceptions.InvalidDeviceTypeException;
import com.nc.users.Role;
import com.nc.users.User;
import com.nc.exceptions.InvalidIpAddressException;
import com.nc.exceptions.RouteNotFoundException;
import com.nc.exceptions.login.IncorrectLoginDataException;
import com.nc.exceptions.login.LoginIsAlreadyInUseException;
import com.nc.exceptions.logout.LogoutFromNotLoggedInUserException;
import com.nc.exceptions.registration.LoginIsAlreadyExistException;
import com.nc.exceptions.route.*;
import com.nc.network.pathElements.IPathElement;
import com.nc.network.pathElements.activeElements.ActiveElement;
import com.nc.network.pathElements.activeElements.Firewall;
import com.nc.network.pathElements.activeElements.IpAddress;
import com.nc.network.pathElements.passiveElements.PassiveElement;
import com.nc.routeProviders.IRouteProvider;
import com.nc.routeProviders.RouteProviderFactory;
import com.nc.routeProviders.RouteProviderType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NetworkTest implements INetworkTest {
    private final Map<String, Network> networks;
    private final RouteProviderFactory routeProviderFactory;
    public final Map<String, User> users;
    private final ThreadPoolExecutor threadPoolExecutor;

    public NetworkTest() {
        this.routeProviderFactory = new RouteProviderFactory();
        this.networks = new HashMap<>();
        this.threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                300L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
        this.users = new ConcurrentHashMap<>();;
        loadUsersData();
    }

    private void loadNetwork(String networkName) throws IOException, ClassNotFoundException {
        if (networks.containsKey(networkName)) {
            return;
        }
        FileInputStream fileInputStream =
                new FileInputStream("src/main/resources/" + networkName + ".ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Network network = (Network) objectInputStream.readObject();

        if (network == null) {
            throw  new ClassNotFoundException();
        }
        networks.put(networkName, network);
        objectInputStream.close();
    }

    @Override
    public void route(String net,
                      String routeProvider,
                      String sender,
                      String recipient,
                      boolean isIp,
                      boolean isOnlyActive,
                      IClient client)
            throws RemoteException {
        Supplier<List<String>> routeTask = () -> {
            try {
                return getRouteTask(net,
                        routeProvider,
                        sender,
                        recipient,
                        isIp,
                        isOnlyActive);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        };

        BiConsumer<List<String>, Throwable> handler = (r, e) -> {
            try {
                if (r != null) {
                    client.printResponse(r);
                } else if (e != null) {
                    client.printResponse(e.getCause());
                }
            } catch (RemoteException ex) {
                throw new CompletionException(ex);
            }
        };

        try {
            CompletableFuture.supplyAsync(routeTask, threadPoolExecutor)
                    .whenComplete(handler);
        } catch (CompletionException ex) {
            throw (RemoteException) ex.getCause();
        }
    }

    private List<String> getRouteTask(String netName,
                                     String routeProviderName,
                                     String senderParameter,
                                     String recipientParameter,
                                     boolean isIp,
                                     boolean isOnlyActive)
            throws ElementNotFoundException,
            NoSuchRouteProviderException,
            NoSuchNetworkException {
        Network net;
        IRouteProvider routeProvider;
        ActiveElement sender;
        ActiveElement recipient;

        //Load Network
        try {
            loadNetwork(netName);
            net = networks.get(netName);
        } catch (Exception e) {
            throw new NoSuchNetworkException();
        }

        //Load Sender and Recipient
        sender = getActiveElement(isIp, senderParameter, net);
        recipient = getActiveElement(isIp, recipientParameter, net);

        //LoadRouteProvider
        routeProvider = getRouteProvider(routeProviderName, sender);
        List<IPathElement> route =
                getRoute(net, routeProvider, sender, recipient, isOnlyActive);

        if (route == null) {
            return null;
        }

        List<String> res = new ArrayList<>();

        for (IPathElement pathElement : route) {
            res.add(pathElement.toString());
        }
        return res;
    }

    private ActiveElement getActiveElement( boolean isIp,
                                            String pathElementParameter,
                                            Network net)
            throws ElementNotFoundException {
        ActiveElement activeElement;

        if (isIp) {
            try {
                activeElement = net.getPathElementByIp(new IpAddress(pathElementParameter));
            } catch (InvalidIpAddressException e) {
                throw new ElementNotFoundException();
            }
        } else {
            if (!NumberUtils.isCreatable(pathElementParameter)){
                throw new ElementNotFoundException();
            }
            int pathElementId = Integer.parseInt(pathElementParameter);
            activeElement = net.getPathElementById(pathElementId);
        }

        if (activeElement == null)
            throw new ElementNotFoundException("The element with specified id \""
                    + pathElementParameter
                    + "\" doesn't exist or is not an active element.");

        return activeElement;
    }

    private IRouteProvider getRouteProvider(String routeProviderName, ActiveElement sender)
            throws NoSuchRouteProviderException {
        if (sender.hasActualRouteProvider()
                && sender.getCachedRouteProviderName().equals(routeProviderName)) {
            return sender.getCachedRouteProvider();
        } else {
            RouteProviderType rpt = RouteProviderType.fromString(routeProviderName);

            if (rpt == null) {
                throw new NoSuchRouteProviderException("The route provider with name \""
                        + routeProviderName + "\" was not found");
            }
            return routeProviderFactory.createRouteProvider(rpt);
        }
    }

    private List<IPathElement> getRoute(Network net,
                                        IRouteProvider routeProvider,
                                        IPathElement sender,
                                        IPathElement recipient,
                                        boolean isOnlyActive) {
        List<IPathElement> route;

        try {
            route = routeProvider.getRoute(net, sender, recipient);
        } catch (RouteNotFoundException e) {
            System.out.println("The route between the two nodes was not found");
            return null;
        }

        if (isOnlyActive) {
            for (int i = 0; i < route.size(); i++) {
                if (route.get(i) instanceof PassiveElement)
                    route.remove(route.get(i));
            }
        }
        return route;
    }

    public void changeBannedList(String netName,
                                 Integer firewallId,
                                 Integer bannedElementId,
                                 boolean isDelete)
            throws ElementNotFoundException, InvalidDeviceTypeException, NoSuchNetworkException {
        try {
            loadNetwork(netName);
        } catch (Exception e) {
            System.out.println("Changing failed.");
        }

        Network net = networks.get(netName);
        if (net == null) {
            throw new NoSuchNetworkException();
        }

        ActiveElement activeElement = net.getPathElementById(firewallId);
        IPathElement bannedElement = net.getPathElementById(bannedElementId);

        if (activeElement == null || bannedElement == null) {
            throw new ElementNotFoundException();
        }

        if (activeElement instanceof Firewall) {
            Firewall firewall = (Firewall) activeElement;

            if (isDelete) {
                firewall.removeBannedElement(bannedElement);
            } else {
                firewall.addBannedElement(bannedElement);
            }
        } else {
            throw new InvalidDeviceTypeException();
        }
        System.out.println("Changing successful");
    }

    @Override
    public String login(String login, String password)
            throws LoginIsAlreadyInUseException, IncorrectLoginDataException {
            try {
                if (users.get(login).isActive()) {
                    throw new LoginIsAlreadyInUseException();
                } else {
                    users.get(login).setActive(true);
                }

                //Checking for password equivalence.
                if (users.get(login).isPasswordEqualsTo(password)) {
                    return login;
                } else {
                    users.get(login).setActive(false);
                    throw new IncorrectLoginDataException();
                }
            } catch (NullPointerException ex) {
                throw new IncorrectLoginDataException();
            }
    }

    @Override
    public void logout(String login) throws LogoutFromNotLoggedInUserException {
        if (login == null) throw new LogoutFromNotLoggedInUserException();
        User user = users.get(login);

        if (user != null && user.isActive()) {
            user.setActive(false);
        } else {
            throw new LogoutFromNotLoggedInUserException();
        }
    }

    public void refreshAllCachedRouteProvidersOf(String netName) {
        networks.get(netName).refreshAllCachedRouteProviders();
    }

    public void configFirewall(String netName,
                               String firewallIdStr,
                               String bannedElementIdStr,
                               String loggedAdminName,
                               boolean isDelete) {


        if (loggedAdminName == null || users.get(loggedAdminName) == null) {
            System.out.println("You should authorize to config firewall.");
            return;
        }

        User admin = users.get(loggedAdminName);

        if (!admin.getRoles().contains(Role.ADMIN)) {
            System.out.println("You must log in under an account that has the role of " +
                    "an administrator to configure the firewall");
            return;
        }

        if (!StringUtils.isNumeric(firewallIdStr)
                || !StringUtils.isNumeric(bannedElementIdStr)) {
            System.out.println("The element Ids must be numbers");
            return;
        }

        System.out.println("Firewall configuration started.");

        Integer firewallId = Integer.parseInt(firewallIdStr);
        Integer bannedElementId = Integer.parseInt(bannedElementIdStr);
        try {
            changeBannedList(netName, firewallId, bannedElementId, isDelete);
        } catch (ElementNotFoundException ex) {
            System.out.println("Element(s) with specified Id(s) not found");
            return;
        } catch (InvalidDeviceTypeException ex) {
            System.out.println("Specified device isn't a firewall");
            return;
        } catch (NoSuchNetworkException ex) {
            System.out.println("network with specified name not found");
            return;
        }
        refreshAllCachedRouteProvidersOf(netName);
        System.out.println("Firewall configuration completed successfully.");
    }

    public void saveUsersData() {
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/usersData.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);

            objectOutputStream.writeInt(users.size());

            for (Map.Entry<String, User> admin : users.entrySet()) {
                objectOutputStream.writeObject(admin.getKey());
                objectOutputStream.writeObject(admin.getValue());
            }

            objectOutputStream.close();
        } catch (Exception exception) {
            System.out.println("Failed to save users's data.");
        }
    }

    private void setUpLoginData() {
        try {
            FileOutputStream fileInputStream =
                    new FileOutputStream("src/main/resources/usersData.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileInputStream);
            registration("admin", "password");
            objectOutputStream.writeInt(users.size());
            objectOutputStream.writeObject("admin");
            objectOutputStream.writeObject(users.get("admin"));

            fileInputStream.close();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

    }

    private void loadUsersData() {
        try {
            FileInputStream fileInputStream =
                    new FileInputStream("src/main/resources/usersData.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            int adminsCount = objectInputStream.readInt();

            for (int i = 0; i < adminsCount; i++) {
                String key = (String) objectInputStream.readObject();
                User value = (User) objectInputStream.readObject();
                users.put(key, value);
            }

            objectInputStream.close();
            System.out.println("Users data uploaded successfully");
        } catch (Exception e) {
            System.out.println("Failed to load users data.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void registration(String login, String password) throws LoginIsAlreadyExistException {
        if (users.containsKey(login)) {
            throw new LoginIsAlreadyExistException();
        }
        users.put(login, new User(login, password, Role.CLIENT));
    }
}