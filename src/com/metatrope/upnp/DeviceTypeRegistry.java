package com.metatrope.upnp;

import java.util.HashMap;
import java.util.Map;

public class DeviceTypeRegistry {
    private Map<String, UPnpDeviceFactory> factoryRegistry = new HashMap<>();
    private Map<String, UPnpDeviceDiscoverer<? extends UPnpDevice>> discoveryRegistry = new HashMap<>();
    
    public UPnpDevice create(DeviceDescriptionFile ddf) {
        String deviceType = ddf.getDeviceType();
        UPnpDeviceFactory factory = factoryRegistry.get(deviceType);
        if (factory == null) {
            factory = new UPnpDevice.Factory();
        }
        return factory.create(ddf);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void notify(UPnpDevice device) {
        UPnpDeviceDiscoverer discoverer = discoveryRegistry.get(device.getDeviceType());
        if (discoverer != null) {
            discoverer.onDeviceDiscovered(device);
        }
    }
    
    public <T> void registerDeviceType(String deviceType, UPnpDeviceFactory factory, UPnpDeviceDiscoverer<? extends UPnpDevice> discoverer) {
        factoryRegistry.put(deviceType, factory);
        discoveryRegistry.put(deviceType, discoverer);
    }
}
