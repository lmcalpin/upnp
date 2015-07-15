package com.metatrope.upnp;

public interface UPnpDeviceFactory {
    public UPnpDevice create(DeviceDescriptionFile ddf);
}
