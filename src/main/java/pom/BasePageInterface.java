package pom;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public interface BasePageInterface {

   void setNumberTextBox(MobileElement numberTextBox, String insertedNumber);
    void setDriver(AppiumDriver<MobileElement> driver);

}
