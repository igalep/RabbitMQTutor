package connection;


import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriverException;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidManager extends DriverManager {

    public AndroidManager(String automationName, String devicetype) {
        super(devicetype);
        automationType = automationName;
    }

    @Override
    public void capabilitiesAddition() {

        //desiredCapabilities.setCapability("app",getAppString("path_apk"));
        desiredCapabilities.setCapability("platformVersion", readDeviceFile.getString("androidVer"));
        try {
            driver = new AndroidDriver(new URL(url), desiredCapabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (WebDriverException e){
            System.err.println("---ERROR: WITH ANDROID DRIVER due to --> " + e.getMessage());
            driver = null;
            throw new NullPointerException();
        }
        setWebDriverWait();
    }
    }
