package com.metatrope.wemo;

import com.metatrope.upnp.DeviceDescriptionFile;

public class WemoLink extends WemoDevice {
    public static final String DEVICE_TYPE = "urn:Belkin:device:bridge:1";

    public WemoLink(DeviceDescriptionFile ddf) {
        super(ddf);
    }
    
    // TODO
    private String getBridgedDevices(String udn) {
        return "<?xml version=\"1.0\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
                + "<s:Body>"
                + "<u:GetEndDevices xmlns:u=\"urn:Belkin:service:bridge:1\"><ReqListType>SCAN_LIST</ReqListType><DevUDN>" + udn + "</DevUDN></u:GetEndDevices>"
                + "</s:Body></s:Envelope>";
    }
}
