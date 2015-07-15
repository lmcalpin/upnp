package com.metatrope.upnp;

public interface UPnpDeviceDiscoverer<T> {
    public void onDeviceDiscovered(T device);
}
