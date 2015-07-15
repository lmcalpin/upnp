package com.metatrope.util;

public class SoapUtil {
    public static String soap(String body) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                    "<s:Body>" +
                        body +
                    "</s:Body>" +
                "</s:Envelope>";
    }
}
