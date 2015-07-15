package com.metatrope.upnp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.io.ByteStreams;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A UPnP device located on the local network.
 * @author Lawrence McAlpin (admin@lmcalpin.com)
 */
public class UPnpDevice {
    private final String deviceType;
    private final String friendlyName;
    private final String baseUrl;
    private final String serialNumber;
    private final String description;
    
    private final List<UPnpService> services;
    private final Map<String, UPnpService> serviceMap;
    
    public UPnpDevice(DeviceDescriptionFile ddf) {
        URI docURL = URI.create(ddf.getUrl());
        URI baseURL = docURL.resolve(".");
        this.baseUrl = baseURL.toASCIIString();
        this.deviceType = ddf.getDeviceType();
        this.friendlyName = ddf.getString("//device/friendlyName");
        this.serialNumber = ddf.getString("//device/serialNumber");
        this.description = ddf.getString("//device/modelDescription");
        this.services = new ArrayList<>();
        this.serviceMap = new HashMap<>();
        NodeList serviceNodes = ddf.getNodes("//serviceList/service");
        for (int i = 0; i < serviceNodes.getLength(); i++) {
            Node node = serviceNodes.item(i);
            String serviceType = ddf.getString(node, "serviceType");
            String serviceId = ddf.getString(node, "serviceId");
            String scpdURL = baseURL.resolve(ddf.getString(node, "SCPDURL")).toASCIIString();
            String controlURL = baseURL.resolve(ddf.getString(node, "controlURL")).toASCIIString();
            String eventURL = baseURL.resolve(ddf.getString(node, "eventSubURL")).toASCIIString();
            UPnpService service = new UPnpService(serviceType, serviceId, scpdURL, controlURL, eventURL);
            serviceMap.put(serviceType, service);
            services.add(service);
        }
    }
    
    public static class Factory implements UPnpDeviceFactory {
        @Override
        public UPnpDevice create(DeviceDescriptionFile ddf) {
            return new UPnpDevice(ddf);
        }
    }
    
    public String getDeviceType() {
        return deviceType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getUrl() {
        return baseUrl;
    }
    
    public String getDescription() {
        return description;
    }

    public List<UPnpService> getServices() {
        return services;
    }

    public UPnpService getService(String serviceId) {
        return serviceMap.get(serviceId);
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    
    public String post(String serviceType, String soapAction, String xml) {
        String url = serviceMap.get(serviceType).getControlURL();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection)new URL(url).openConnection();
            urlConnection.setRequestProperty("SOAPACTION", "\"" + soapAction + "\"");
            urlConnection.setRequestProperty("Accept", "");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(xml.length()));
            urlConnection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            System.out.println(soapAction + "\n" + xml);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(xml.getBytes());
            InputStream inputStream = urlConnection.getInputStream();
            return new String(ByteStreams.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
