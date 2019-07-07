package connection;

import common.JsonUtil;
import dto.DeviceData;
import infra.AppiumServer;
import infra.Configuration;
import io.appium.java_client.AppiumDriver;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.DesiredCapabilities;
import util.PropertiesReaderSingleton;

import java.io.IOException;
import java.nio.file.Paths;


import static common.JsonUtil.*;


/**
 * Created by epshtein.
 * Date: 2018-12-26
 */
public abstract class DriverManager {
    protected AppiumDriver driver;
    protected CustomWebDriver webDriverWait;
    protected DesiredCapabilities desiredCapabilities;
    protected String automationType;
    protected JSONObject device;
    protected JSONObject devices;
    protected String deviceType;
    protected JSONObject connections;
    protected JSONObject capability;
    protected JSONObject readDeviceFile;
    protected String osDevices;
    protected JSONObject readCapabilityFile;
    protected JSONObject subURL;
    protected String udid;
    protected String serverPort;
    protected String url;
    protected AppiumServer appiumServer;


    public DriverManager(String devicetype) {

        desiredCapabilities = new DesiredCapabilities();
        device = null;
        capability = null;
        deviceType= devicetype;
        connections = readFile( "connectionJson.json");
        subURL= getSubJSON(connections,"local-ANDROID");
        readCapabilityFile = readFile("capabilitiesJson.json");
        devices = readFile("deviceJson.json");
        readDeviceFile = getSubJSON(devices,devicetype);
        osDevices = readDeviceFile.getString("os");
        udid =  (readDeviceFile.getString("udid"));
        appiumServer = new AppiumServer();
        serverPort = readDeviceFile.getString("serverPort");
        url = JsonUtil.urlForDevice(serverPort);

    }


    protected void setWebDriverWait() {
        webDriverWait = new CustomWebDriver(driver);
        if (readDeviceFile.equals(null) || connections.equals(null) ||readCapabilityFile.equals(null)){
            Assert.fail("---ERROR: WITH READING JSON FILE INTO JSONOBJECT !!!");
        }
    }


    /**
     * get AppiumDriver (iOS / Android) + customWebdriver
     *
     * @return Pair<AppiumDriver <>, CustomWebDriver> with driver and custom webdriver
     */
    public DeviceData getDriverInfo() {

        createDriver();

        DeviceData deviceData = new DeviceData(driver,webDriverWait);

        return deviceData;
    }

    public void createDriver() {

        capability = getSubJSON(readCapabilityFile,osDevices);
        desiredCapabilities.setCapability("automationName", automationType);
        desiredCapabilities.setCapability("deviceName", deviceType);
        desiredCapabilities.setCapability("udid",udid);
        capability.keySet().stream().forEach(key -> desiredCapabilities.setCapability(key, capability.get(key).toString()));
        capabilitiesAddition();
    }


    protected String getAppString(String appType){
        return Paths.get(PropertiesReaderSingleton.getInstance().getPropertyAsString(appType)).toAbsolutePath().normalize().toString();
    }

    public abstract void capabilitiesAddition();

    public void openAppiumServer(String port){
        Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("appium -p "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}