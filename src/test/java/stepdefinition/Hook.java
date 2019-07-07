package stepdefinition;
import common.ProjectPaths;
import connection.DriverManagerFactory;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import dto.DeviceData;
import enums.Platform;
import infra.AppiumServer;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.json.JSONObject;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import utils.ScreenshotUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static common.JsonUtil.getSubJSON;
import static common.JsonUtil.readFile;

public class Hook extends GlobalStepDefinition {

    protected JSONObject readDeviceFile;
    private static GlobalStepDefinition globalStepDefinition ;
    private DateFormat df;
    private List<AppiumDriverLocalService> services;
    private List<String> devicesTypes ; //think how to insert the devices
    private AppiumServer appiumServer;
    private AppiumDriverLocalService service;



    public Hook(GlobalStepDefinition globalStepDefinition) {
        this.globalStepDefinition = globalStepDefinition;
        readDeviceFile = readFile("deviceJson.json");
        df = new SimpleDateFormat("dd-MMM-yyyy__hh_mm aa");
        devicesTypes = new ArrayList<>();
        appiumServer= new AppiumServer();
        services =new ArrayList<>();
    }

    @Before ("@crossplatform")
    public void LocalsetUp() {

        List<String> devicesTypes = new ArrayList<>();  //think how to insert the devices
        devicesTypes.add("pixel3-STAV" );
        devicesTypes.add("pixel3-STAV_2");

        for (String device:devicesTypes) {
            JSONObject deviceDetails = getSubJSON(readDeviceFile, device);
            int serverPort = deviceDetails.getInt("serverPort");
            try {
                service = appiumServer.initServer(serverPort);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            DeviceData deviceData = DriverManagerFactory.getDriverManager(deviceDetails.getString("os"), device).getDriverInfo();
            deviceData.setAppiumDriverLocalService(service);
            globalStepDefinition.setDeviceData(device,deviceData);
            //devicesTypes.forEach(i-> globalStepDefinition.driverInformation.put(i,DriverManagerFactory.getDriverManager(getSubJSON(readDeviceFile,i).getString("os"),i,devicesTypes.indexOf(i)).getDriverInfo()));
        }

}


    @After("@crossplatform")
    public void scenarioTearDownLocal(Scenario scenario) {
        for (String device:devicesTypes) {

//            if (globalStepDefinition.driverInformation.get(deviceType).getAppiumDriver() == null) {
////                Assert.fail("---ERROR: WITH APPIUM DRIVER INITIALIZATION !!!");
////                //TODO adding log for critical ---ERROR !!
////            }
//
//            String folderName = ProjectPaths.artifacts;
//            try {
//                File directory = new File(folderName);
//
//                if (!Files.exists(directory.toPath())) {
//
//                    Files.createDirectory(directory.toPath());
//                }
//
////            String recordedMovie1;
//
//                switch (Platform.valueOf(osType)) { //should be changed once iOS will have a recorder
//                    case ANDROID:
//                        try { //temp solution to be unified
////                        recordedMovie1 = deviceRecorder1.stopRecord();
//                            takeAppiumLog(deviceType);
////                        if (scenario.isFailed()) {
////                            takeVideoFile(scenario, recordedMovie);
////                            takeScreenShot(scenario,deviceType);
////                        }
//                            break;
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            for(AppiumDriverLocalService service: services){
//                service.stop();
//            }
//            tearDown(deviceType);
        }
    }


//    private void takeVideoFile(Scenario scenario, String recordedMovie) {
//        try {
//            deviceRecorder.createMovieFile(ProjectPaths.artifacts + "video/" + scenario.getName(), recordedMovie);
//
//        } catch (IOException e) {
//            System.err.println("---ERROR with movie recorder");
//        }
//    }

//
//
//    private void takeScreenShot(Scenario scenario,String deviceType) {
//        String folderName = ProjectPaths.artifacts + "screenShots";
//        File directory = new File(folderName);
//        if (!Files.exists(directory.toPath())) {
//            try {
//                Files.createDirectory(directory.toPath());
//            } catch (IOException e) {
//                e.printStackTrace(); }
//        }
//        String file_name = new StringBuilder().append(df.format(new Date())).
//                append("-").
//                append(scenario.getName()).toString();
//        byte[] screenShot = ScreenshotUtils.takeMobileScreenshot(globalStepDefinition.driverInformation.get(deviceType).getAppiumDriver());
//        if (screenShot != null) {
//            scenario.write("Scenario " + scenario.getName() + " has failed");
//            scenario.embed(screenShot, "image/png"); //embedding screen shot to cucumber report
//        }
//        ScreenshotUtils.takeScreenshot(globalStepDefinition.driverInformation.get(deviceType).getAppiumDriver(), folderName, file_name);//creating the screenshot as a file and storing it in screenShots folder
//    }


    /**
     * Creates appium log file under executionArtifacts .
     */
//    private void takeAppiumLog(String deviceType) {
//        String folderName = ProjectPaths.artifacts + "appium";
//        try {
//            File directory = new File(folderName);
//
//            if (!Files.exists(directory.toPath())) {
//                Files.createDirectory(directory.toPath());
//            }
//            String file_name = new StringBuilder().append(df.format(new Date())).
//                    append("-Appium.log").toString();
//
//            LogEntries logEntries = globalStepDefinition.driverInformation.get(deviceType).getAppiumDriver().manage().logs().get("server");
//            if (logEntries != null) {
//                String content = logEntries.getAll().stream().map(LogEntry::toString).collect(Collectors.joining("\n"));
//                Files.write(Paths.get(directory + "/" + file_name), content.getBytes());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("---ERROR Could not generate appium logs - please check if --relaxed-security flag is set ");
//        }
//    }
//
//    public void tearDown(String deviceType) {
//        globalStepDefinition.driverInformation.get(deviceType).getAppiumDriver().quit();
//    }




}
