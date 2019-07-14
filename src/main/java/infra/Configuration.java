package infra;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private static Properties config = new Properties();

    private Configuration() {
        throw new AssertionError("This class should not have instances");
    }

    private static Properties getConfig() {
        if (config.size() == 0) {
            config = initConfiguration();
        }
        return config;
    }

    private static Properties initConfiguration() {
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/settings.properties")) {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("File doesn't exists");
            throw new UncheckedIOException("Properties file read problems", e);
        }
        return config;
    }

    public static String getAppiumPath() {
        return getConfig().getProperty("appiumPath");
    }

    public static String getDeviceName1() {
        return getConfig().getProperty("deviceName1");
    }

    public static String getDeviceName2() {
        return getConfig().getProperty("deviceName2");
    }
    private void setDeviceName1(String name) {
        getConfig().setProperty("deviceName1", name);
    }
    private void setDeviceName2(String name) {
        getConfig().setProperty("deviceName2", name);
    }

    public static String getudid1() {
        return getConfig().getProperty("udid1");
    }
    public static String getudid2() {
        return getConfig().getProperty("udid2");
    }

    private void setudid(String id) {
        getConfig().setProperty("udid", id);
    }

    public static String getPlatformName() {
        return getConfig().getProperty("platformName");
    }

    private void setPlatformName(String name) {
        getConfig().setProperty("platformName", name);
    }

    public static String getPlatformVersion() {
        return getConfig().getProperty("platformVersion");
    }

    private void setPlatformVersion(String version) {
        getConfig().setProperty("platformVersion", version);
    }

    public static String getServerUrl() {
        return String.format("http://%s:%s/wd/hub",
                getServerIp(), getServerPort());
    }

    public static String getServerIp() {
        return getConfig().getProperty("serverIP");
    }

    public static int getServerPort() {
        return Integer.parseInt(getConfig().getProperty("serverPort1"));
    }

    public static String getAppPackage() {
        return getConfig().getProperty("appPackage");
    }

    public static String getAppActivity() {
        return getConfig().getProperty("appActivity");
    }

    public static String skipUnlock() {
        return getConfig().getProperty("skipUnlock");
    }

    public static String noReset() {
        return getConfig().getProperty("noReset");
    }
}
