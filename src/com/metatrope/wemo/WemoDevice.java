package com.metatrope.wemo;

import com.metatrope.upnp.DeviceDescriptionFile;
import com.metatrope.upnp.UPnpDevice;
import com.metatrope.util.SoapUtil;

public class WemoDevice extends UPnpDevice {
    private String firmwareVersion;
    private String friendlyName;
    private String udn;
    
    public WemoDevice(DeviceDescriptionFile ddf) {
        super(ddf);
        this.firmwareVersion = ddf.getString("//device/firmwareVersion");
        this.friendlyName = ddf.getString("//device/friendlyName");
        this.udn = ddf.getString("//device/UDN");
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }
    
    public String getUniqueDeviceNumber() {
        return udn;
    }
    
    public String getSignalStrength() {
        return postSimpleRequest(this, "urn:Belkin:service:basicevent:1", "GetSignalStrength");
    }

    protected String postSimpleRequest(WemoDevice device, String serviceType, String action) {
        return device.post(serviceType, serviceType + "#" + action, getSimpleRequestXml(serviceType, action));
    }
    
    private String getSimpleRequestXml(String serviceType, String action) {
        return SoapUtil.soap("<u:" + action + " xmlns:u=\"" + serviceType + "\"></u:" + action + ">");
    }
}
