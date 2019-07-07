package connection;

import io.appium.java_client.remote.MobilePlatform;

public final class DriverManagerFactory {
    private DriverManagerFactory() {

        throw new UnsupportedOperationException();
    }

    private static DriverManager driverManager = null;

    public static DriverManager getDriverManager(String osType, String deviceType) {
        /**need to be figured out */

        return new AndroidManager("UiAutomator2", deviceType);
    }
}