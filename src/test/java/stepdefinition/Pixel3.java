package stepdefinition;

import com.rabbitmq.client.*;
import connection.DriverManagerFactory;
import dto.DeviceData;
import infra.AppiumServer;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.json.JSONObject;
import pom.EditMessage;
import pom.Messages;
import pom.NewChatPage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import static common.JsonUtil.getSubJSON;


public class Pixel3 implements Runnable{

    private final String RPC_QUEUE_NAME = "rpc_queue";
    protected ConnectionFactory factory;
    protected String deviceA;
    protected String deviceB;
    protected JSONObject devicDetails;
    private AppiumDriver ad;
    private AppiumDriverLocalService service;
    private AppiumServer appiumServer= new AppiumServer();
    private DeviceData deviceData;
    private JSONObject deviceDetails;
    private URL urlPixel3_S;
    String response;


    public Pixel3(String device_a, String device_b, JSONObject readDeviceFile ) {
        factory= new ConnectionFactory();
        factory.setHost("localhost");
        deviceA = device_a;
        deviceB = device_b;
        devicDetails = readDeviceFile;
        deviceDetails = getSubJSON(devicDetails, deviceA);
        try {
            service = appiumServer.initServer();
            urlPixel3_S = appiumServer.getServerURL();
        } catch (FileNotFoundException e) {
            e.printStackTrace(); }
        deviceData = DriverManagerFactory.getDriverManager(deviceDetails.getString("os"), deviceA,urlPixel3_S).getDriverInfo();
        deviceData.setAppiumDriverLocalService(service);
        ad = deviceData.getAppiumDriver();
        response = "";
    }
    @Override
    public void run(){

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" ["+deviceA+"] Awaiting for "+deviceB+" message");

            Object monitor = new Object();

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                try {
                    String message = new String(delivery.getBody(), "UTF-8");

                    if (message.contains("time")){
                        checkTime();
                    }
                    if (message.contains("doing")){
                        checkDoing();
                    }

                } catch (RuntimeException e) {
                    System.out.println(" ["+deviceA+"] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        deviceData.getAppiumDriver().quit();
        deviceData.getAppiumDriverLocalService().stop();
    }

    public void checkTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HHmm");

        Messages messages = new Messages(ad);
        NewChatPage newChatPage = messages.newChat();
        newChatPage.setSearchContact(deviceB);
        EditMessage editMessage = newChatPage.chooseContact();
        String message ="The time is- "+format.format(date);
        editMessage.addMessage(message);
     response = " ["+deviceA+"] The time is- "+format.format(date);
        System.out.println(response);
    }

    public void checkDoing(){

        EditMessage editMessage = new EditMessage(ad);
        String message2 = "I am currently working on new project";
        editMessage.addMessage(message2);
       response = " ["+deviceA+"] "+message2;
        System.out.println(response);
    }
}