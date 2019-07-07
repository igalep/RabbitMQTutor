package dto;

import connection.CustomWebDriver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.javatuples.Pair;

public class DeviceData {

    protected Pair<AppiumDriver<MobileElement>, CustomWebDriver> driverPair;
    protected AppiumDriverLocalService appiumDriverLocalService;


    public DeviceData(AppiumDriver ad, CustomWebDriver cw){
        driverPair=new Pair<>(ad,cw);
    }



    public void setAppiumDriver(AppiumDriver appiumDriver) {
        driverPair.setAt0(appiumDriver);
    }

    public void setCustomWebDriver(CustomWebDriver customWebDriver) {
        driverPair.setAt1(customWebDriver);
    }

    public AppiumDriver getAppiumDriver(){
        return driverPair.getValue0();
    }


    public CustomWebDriver getCustomWebDriver(){
        return driverPair.getValue1();
    }

    public void setAppiumDriverLocalService (AppiumDriverLocalService appiumdriverlocalService){
        appiumDriverLocalService = appiumdriverlocalService;
    }

    public AppiumDriverLocalService getAppiumDriverLocalService(){
        return  appiumDriverLocalService;
    }

}
