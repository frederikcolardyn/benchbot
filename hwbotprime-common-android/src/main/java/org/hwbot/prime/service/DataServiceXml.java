package org.hwbot.prime.service;

import java.util.Map;

import org.hwbot.bench.security.EncryptionModule;
import org.hwbot.prime.model.DeviceInfo;
import org.hwbot.prime.model.PersistentLogin;

public class DataServiceXml {

    private static DataServiceXml service;

    private DataServiceXml() {
    }

    public static DataServiceXml getInstance() {
        if (service == null) {
            service = new DataServiceXml();
        }
        return service;
    }

    public static String createXml(String version, Number score, DeviceInfo deviceInfo, PersistentLogin credentials, EncryptionModule encryptionModule) {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<submission xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://hwbot.org/submit/api\">");
        xml.append("<application>");
        xml.append("<name>HWBOT Prime</name>");
        xml.append("<version>" + version + "</version>");
        xml.append("</application>");
        if (credentials != null) {
            xml.append("<credentials>");
            xml.append("<token>" + credentials.getToken() + "</token>");
            xml.append("</credentials>");
        }
        xml.append("<score><points>" + score + "</points></score>");
        xml.append("<hardware>");
        xml.append("<device>");
        if (deviceInfo.getName() != null) {
            if (deviceInfo.getId() != null) {
                xml.append("<id>" + deviceInfo.getId() + "</id>");
            }
            xml.append("<name><![CDATA[" + deviceInfo.getName() + "]]></name>");
        }
        xml.append("</device>");
        if (deviceInfo.getProcessor() != null) {
            xml.append("<processor>");
            if (deviceInfo.getProcessorId() != null) {
                xml.append("<id>" + deviceInfo.getProcessorId() + "</id>");
            }
            xml.append("<name><![CDATA[" + deviceInfo.getProcessor() + "]]></name>");
            if (deviceInfo.getProcessorClock() != null) {
                xml.append("<coreClock><![CDATA[" + (deviceInfo.getProcessorClock().intValue()) + "]]></coreClock>");
            }
            xml.append("</processor>");
        }
        if (deviceInfo.getVideocard() != null) {
            xml.append("<videocard>");
            if (deviceInfo.getVideocard() != null) {
                xml.append("<id>" + deviceInfo.getVideocardId() + "</id>");
            }
            xml.append("<name><![CDATA[" + deviceInfo.getVideocard() + "]]></name>");
            xml.append("</videocard>");
        }
        xml.append("</hardware>");
        xml.append("<software>");
        xml.append("<os>");
        xml.append("<family>" + AndroidHardwareService.OS + "</family>");
        xml.append("</os>");
        xml.append("</software>");

        xml.append("<metadata name=\"java_environment\">");
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            xml.append(String.format("%s=%s%n\n", envName, env.get(envName)));

        }
        xml.append("</metadata>");

        xml.append("</submission>");
        return xml.toString();
    }

}