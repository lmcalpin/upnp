package com.metatrope.upnp;

public class UPnpService {
    private final String serviceType;
    private final String serviceId;
    private final String scpdURL;
    private final String controlURL;
    private final String eventURL;

    public UPnpService(String serviceType, String serviceId, String spcdURL, String controlURL, String eventURL) {
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.scpdURL = spcdURL;
        this.controlURL = controlURL;
        this.eventURL = eventURL;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getScpdURL() {
        return scpdURL;
    }

    public String getControlURL() {
        return controlURL;
    }

    public String getEventURL() {
        return eventURL;
    }
}
