package stepdefinition;

import com.rabbitmq.client.*;
import connection.DriverManagerFactory;
import dto.DeviceData;
import infra.AppiumServer;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.json.JSONObject;
import org.openqa.selenium.By;

import java.io.FileNotFoundException;
import java.io.IOException;
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
    Connection connection;


    public Pixel3(String device_a, String device_b, JSONObject readDeviceFile ) {
        factory= new ConnectionFactory();
        factory.setHost("localhost");
        deviceA = device_a;
        deviceB = device_b;
        devicDetails = readDeviceFile;
        deviceDetails = getSubJSON(devicDetails, deviceA);
        int serverPort = deviceDetails.getInt("serverPort");
        try {
            service = appiumServer.initServer(serverPort);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); }
        deviceData = DriverManagerFactory.getDriverManager(deviceDetails.getString("os"), deviceA).getDriverInfo();
        deviceData.setAppiumDriverLocalService(service);
        ad = deviceData.getAppiumDriver();
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

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");

                    if (message.contains("time")){
                        Date date = new Date();
                        DateFormat format = new SimpleDateFormat("HHmm");

                        ad.findElement(By.id("com.google.android.apps.messaging:id/start_new_conversation_button")).click();
                        ad.findElement(By.id("com.google.android.apps.messaging:id/recipient_text_view")).click();
                        ad.getKeyboard().sendKeys(deviceB);
                        Thread.sleep(1000);
//                        ad.findElement(By.id("com.google.android.apps.messaging:id/contact_picker_create_group")).click();
                        ad.findElement(By.id("com.google.android.apps.messaging:id/compose_message_text")).click();
                        ad.findElement(By.id("com.google.android.apps.messaging:id/compose_message_text")).sendKeys("The time is- "+format.format(date));
                        ad.findElement(By.id("com.google.android.apps.messaging:id/send_message_button_icon")).click();
                        response = " ["+deviceA+"] The time is- "+format.format(date) +")";
                        System.out.println(response);
                    }

                } catch (RuntimeException | InterruptedException e) {
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

    public void close() throws IOException {
        deviceData.getAppiumDriver().quit();
        deviceData.getAppiumDriverLocalService().stop();
    }
}