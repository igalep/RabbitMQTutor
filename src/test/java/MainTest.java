import infra.AppiumServer;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stepdefinition.GlobalStepDefinition;
import stepdefinition.Pixel3;
import stepdefinition.Pixel3_XL;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static common.JsonUtil.readFile;

public class MainTest {
    private static GlobalStepDefinition globalStepDefinition = new GlobalStepDefinition();
    protected JSONObject readDeviceFile= readFile("deviceJson.json");
    private String osType;
    private DateFormat df= new SimpleDateFormat("dd-MMM-yyyy__hh_mm aa");
    private List<AppiumDriverLocalService> services =new ArrayList<>();
    private List<String> devicesTypes = new ArrayList<>();  //think how to insert the devices
    private AppiumServer appiumServer= new AppiumServer();
    private AppiumDriverLocalService service;
    private Runnable mr;
    private Runnable ms;
    ExecutorService pool;



    @Before
    public  void setUp() throws IOException, TimeoutException {
        devicesTypes.add("pixel3-XL" );
        devicesTypes.add("pixel3-S");

        mr = new Pixel3(devicesTypes.get(1),devicesTypes.get(0),readDeviceFile);
        ms = new Pixel3_XL(devicesTypes.get(0),devicesTypes.get(1),readDeviceFile);

    }
    @Test
    public void t1() throws Exception {

        pool = Executors.newFixedThreadPool(8);

        pool.execute(mr);
        pool.execute(ms);

        pool.awaitTermination(1 , TimeUnit.MINUTES);
    }

    @After
    public  void tearDown() {
        try {
            ((Pixel3)mr).close();
            ((Pixel3_XL)ms).shutDown();

            pool.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



