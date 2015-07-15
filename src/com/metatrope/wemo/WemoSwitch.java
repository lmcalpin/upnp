package com.metatrope.wemo;

import com.metatrope.upnp.DeviceDescriptionFile;
import com.metatrope.util.XPathUtil;

/**
 * A Belkin Insight switch located on the local network. 
 * @author Lawrence McAlpin (admin@lmcalpin.com)
 */
public class WemoSwitch extends WemoDevice {
    public static final String DEVICE_TYPE = "urn:Belkin:device:insight:1";
    
    public WemoSwitch(DeviceDescriptionFile ddf) {
        super(ddf);
    }
    
    public boolean isOn() {
        String xml = getBinaryState();
        XPathUtil util = new XPathUtil(xml);
        return Integer.valueOf(util.getString("//BinaryState")) > 0;
    }
    
    public void turnOn() {
        post("urn:Belkin:service:basicevent:1", "urn:Belkin:service:basicevent:1#SetBinaryState", setBinaryStateXml(1));
    }
    
    public void turnOff() {
        post("urn:Belkin:service:basicevent:1", "urn:Belkin:service:basicevent:1#SetBinaryState", setBinaryStateXml(0));
    }
    
    public void toggle() {
        post("urn:Belkin:service:basicevent:1", "urn:Belkin:service:basicevent:1#SetBinaryState", setBinaryStateXml(0));
    }
    
    private String getBinaryState() {
        return postSimpleRequest(this, "urn:Belkin:service:basicevent:1", "GetBinaryState");
    }

    private String setBinaryStateXml(int state) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                "<s:Body>" +
                    "<u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\">" +
                    " <BinaryState>" + state + "</BinaryState>" +
                    "</u:SetBinaryState>" +
                "</s:Body>" +
            "</s:Envelope>";
    }
}
