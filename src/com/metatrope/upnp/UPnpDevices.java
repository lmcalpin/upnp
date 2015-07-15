package com.metatrope.upnp;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Search for UPnp devices using SSDP. 
 * @author Lawrence McAlpin (admin@lmcalpin.com)
 */
public class UPnpDevices {
    // IP to contact when trying to communicate with UPnP devices
    public static final String UPNP_MULTICAST_IP = "239.255.255.250";
    public static final int UPNP_PORT = 1900;
    public static final String SSDP_ALL = "ssdp:all";
    public static final String UPNP_ROOTDEVICE = "upnp:rootdevice";
    
    private List<UPnpDevice> devices;
    private List<String> foundDeviceUrl = new ArrayList<>();
    private DeviceTypeRegistry deviceTypeRegistry;
    
    public UPnpDevices() {
        this.devices = new ArrayList<>();
        this.deviceTypeRegistry = new DeviceTypeRegistry();
    }

    public List<UPnpDevice> getDevices() { return devices; }
    
    private String createSearchMessage() {
        StringBuilder packet = new StringBuilder();
        packet.append("M-SEARCH * HTTP/1.1\r\n");
        packet.append("HOST: " + UPNP_MULTICAST_IP + ":" + UPNP_PORT + "\r\n");
        packet.append("MAN: \"ssdp:discover\"\r\n");
        packet.append("MX: ").append("5").append("\r\n");
        packet.append("ST: ").append(UPNP_ROOTDEVICE).append("\r\n").append("\r\n");
        String searchMessage = packet.toString();
        return searchMessage;
    }
    
    public void registerDeviceType(String deviceType, UPnpDeviceFactory factory, UPnpDeviceDiscoverer<? extends UPnpDevice> discoverer) {
        deviceTypeRegistry.registerDeviceType(deviceType, factory, discoverer);
    }

    public void search() {
        byte[] buf = new byte[2048];
        try (MulticastSocket receiveSocket = new MulticastSocket()) {
            receiveSocket.setReuseAddress(true);
            receiveSocket.setTimeToLive(10);
            receiveSocket.joinGroup(InetAddress.getByName(UPNP_MULTICAST_IP));
            receiveSocket.setSoTimeout(20000);
            
            sendSsdpPacket(receiveSocket.getLocalPort());
            
            boolean isListening = true;
            while (isListening) {
                try {
                    DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
                    receiveSocket.receive(responsePacket);
                    
                    byte[] receivedData = new byte[responsePacket.getLength()];
                    System.arraycopy(responsePacket.getData(), 0, receivedData, 0, responsePacket.getLength());
                    String originaldata = new String(receivedData);
                    String location = Iterables.find(Splitter.on('\n').split(originaldata), s -> s.toLowerCase().startsWith("location:"), null);
    
                    if (location != null) {
                        String url = location.split(":", 2)[1].trim();
                        if (!foundDeviceUrl.contains(url)) {
                            foundDeviceUrl.add(url);
                            
                            URLConnection urlConnection = new URL(url).openConnection();
                            InputStream inputStream = urlConnection.getInputStream();
                            
                            UPnpDevice device = deviceTypeRegistry.create(new DeviceDescriptionFile(url, inputStream));
                            devices.add(device);
                            deviceTypeRegistry.notify(device);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    isListening = false;
                } finally {
                    receiveSocket.disconnect();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendSsdpPacket(int port) throws SocketException, UnknownHostException, IOException {
        String searchMessage = createSearchMessage();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isLoopback())
                continue;
            if (!networkInterface.isUp())
                continue;
            if (networkInterface.supportsMulticast()) {
                for (InetAddress addr : Collections.list(networkInterface.getInetAddresses())) {
                    if (addr.isLoopbackAddress())
                        continue;
                    if (!(addr instanceof Inet4Address ))
                        continue;
                    try {
                        try (MulticastSocket sendSocket = new MulticastSocket(null)) {
                            InetSocketAddress socketAddr = new InetSocketAddress(addr.getHostAddress(), port);
                            sendSocket.bind(socketAddr);
                            //sendSocket.joinGroup(InetAddress.getByName(UPNP_MULTICAST_IP));
                            byte[] searchMessageBytes = searchMessage.getBytes();
                            System.out.println("Sending out search packet on " + addr.getHostAddress());
                            DatagramPacket ssdpDiscoverPacket = new DatagramPacket(searchMessageBytes, searchMessageBytes.length, InetAddress.getByName(UPNP_MULTICAST_IP), UPNP_PORT);
                            sendSocket.send(ssdpDiscoverPacket);
                        }
                    } catch (BindException e) {
                        System.out.println("Ignoring : " + addr.getHostAddress());
                    }
                }
            }
        }
    }
}
