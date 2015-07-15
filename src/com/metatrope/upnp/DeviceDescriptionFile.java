package com.metatrope.upnp;

import com.metatrope.util.XPathUtil;

import java.io.InputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A device description file describing a UPnP device on the local network. 
 * @author Lawrence McAlpin (admin@lmcalpin.com)
 */
public class DeviceDescriptionFile {
    private final String url;
    private final String deviceType;
    private final XPathUtil xpath;
    
    public DeviceDescriptionFile(String url, InputStream is) {
        this.xpath = new XPathUtil(is);
        this.deviceType = getString("//device/deviceType");
        this.url = url;
    }
    
    public String getString(String path) {
        return xpath.getString(path);
    }
    
    public String getString(Node node, String path) {
        return xpath.getString(node, path);
    }
    
    public NodeList getNodes(String path) {
        return xpath.getNodes(path);
    }
    
    public String getUrl() { return url; }
    public String getDeviceType() { return deviceType; }
}
