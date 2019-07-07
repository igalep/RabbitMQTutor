package infra;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class AppiumServer {

    private AppiumDriverLocalService service;
    private int port;

    public AppiumDriverLocalService initServer(int serverPort) throws FileNotFoundException {
        port = serverPort;
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("noReset", "true");
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withAppiumJS(new File(Configuration.getAppiumPath()));
        builder.withIPAddress(Configuration.getServerIp());
        builder.usingPort(serverPort);
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");
        service = AppiumDriverLocalService.buildService(builder);
        OutputStream outputStream =new FileOutputStream("server-"+ Integer.toString(port)+ ".txt",true);
        service.addOutPutStream(outputStream);
        service.start();
        return service;
    }

    public void stopServer() {
        service.stop();
    }

    public boolean checkIsServerRunning(Duration timeout) {
        try {
            URL status = new URL(port + "/sessions");
            new UrlChecker().waitUntilAvailable(timeout.toMillis(), TimeUnit.MILLISECONDS, status);
            return true;
        } catch (UrlChecker.TimeoutException e) {
            return false;
        } catch (MalformedURLException e) {
            throw new UncheckedIOException("Can't parse URL", e);
        }
    }

}