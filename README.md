# upnp

This is a simple library to discover UPnP devices, such as Wemo devices, and to control them.

For example, to discover all Wemo switches, and to send a signal to turn them on as each device is discovered, you could use this code:

~~~
        UPnpDevices deviceDiscoverer = new UPnpDevices();
        deviceDiscoverer.registerDeviceType("urn:Belkin:device:insight:1", (ddf) -> new WemoSwitch(ddf),
                    (WemoSwitch s) -> s.turnOn() );
        deviceDiscoverer.search();
~~~
