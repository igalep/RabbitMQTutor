package infra;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.*;
import java.net.URL;

public class AppiumServer {

    private AppiumDriverLocalService service;
    public URL url;

    public AppiumDriverLocalService initServer() throws FileNotFoundException {
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("noReset", "true");
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withAppiumJS(new File(Configuration.getAppiumPath()));
        builder.withIPAddress(Configuration.getServerIp());
        builder.usingAnyFreePort();
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");
        service = AppiumDriverLocalService.buildService(builder);
        url = service.getUrl();
        OutputStream outputStream =new FileOutputStream("server-"+ Integer.toString(url.getPort())+ ".txt",true);
        service.addOutPutStream(outputStream);
        service.start();
        return service;
    }



    public URL getServerURL(){
        return url;
    }
}