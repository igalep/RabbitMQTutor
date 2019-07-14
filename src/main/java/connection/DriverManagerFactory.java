package connection;

import io.appium.java_client.remote.MobilePlatform;

import java.net.URL;

import static javafx.scene.input.DataFormat.URL;

public final class DriverManagerFactory {
    private DriverManagerFactory() {

        throw new UnsupportedOperationException();
    }

    private static DriverManager driverManager = null;

    public static DriverManager getDriverManager(String osType, String deviceType, java.net.URL url) {
        /**need to be figured out */

        return new AndroidManager("UiAutomator2", deviceType, url);
    }
}