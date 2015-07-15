package com.metatrope.wemo;

import com.metatrope.upnp.UPnpDevices;

public class WeMain {

    // Example that looks for Belkin insight switches and turns them all on.
    public static void main(String[] args) throws Exception {
        UPnpDevices deviceDiscoverer = new UPnpDevices();
        deviceDiscoverer.registerDeviceType("urn:Belkin:device:insight:1", (ddf) -> new WemoSwitch(ddf), 
                    (WemoSwitch s) -> s.turnOn() );
        deviceDiscoverer.search();
    }

}
