package stepdefinition;

import connection.CustomWebDriver;
import dto.DeviceData;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.util.HashMap;
import java.util.Map;


public class GlobalStepDefinition {

    protected Map<String, DeviceData> devices = new HashMap<>();




    /**
     * set DeviceData by deviceType (key)
     */
    public void setDeviceData(String deviceType, DeviceData deviceData) {
        devices.put(deviceType,deviceData);
    }


    /**
     * set DeviceData by deviceType (key)
     */
    public DeviceData getDeviceData(String deviceType) {
        return devices.get(deviceType);
    }


    /**
     * get AppiumDriver by deviceType (key)
     * @return AppiumDriver
     */
    public AppiumDriver getAppiumDriver(String deviceType) {

        return devices.get(deviceType).getAppiumDriver();
    }

    /**
     * set AppiumDriver by deviceType (key)
     */
    public void setAppiumDriver(String deviceType,AppiumDriver appiumDriver ) {

        devices.get(deviceType).getAppiumDriver();
    }

    /**
     * get CustomWebDriver by deviceType (key)
     * @return CustomWebDriver
     */
    public CustomWebDriver getCustomWebDriver(String deviceType) {

        return devices.get(deviceType).getCustomWebDriver();
    }

    /**
     * set CustomWebDriver by deviceType (key)
     */
    public void setCustomWebDriver(String deviceType, CustomWebDriver customWebDriver) {

        devices.get(deviceType).setCustomWebDriver(customWebDriver);
    }

    /**
     * get ServerBuilder by deviceType (key)
     * @return CustomWebDriver
     */
    public AppiumDriverLocalService getAppiumDriverLocalService(String deviceType) {

        return devices.get(deviceType).getAppiumDriverLocalService();
    }

    /**
     * set ServerBuilder by deviceType (key)
     */
    public void setAppiumDriverLocalService(String deviceType,AppiumDriverLocalService appiumDriverLocalService) {

        devices.get(deviceType).setAppiumDriverLocalService(appiumDriverLocalService);
    }

}
