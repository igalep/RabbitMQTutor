package stepdefinition;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static common.JsonUtil.getSubJSON;

public class Pixel3_XL implements AutoCloseable, Runnable{

    protected Connection connection;
    protected Channel channel;
    protected String requestQueueName = "rpc_queue";
    protected String deviceA;
    protected String deviceB;
    protected JSONObject devicDetails;
    private AppiumDriver ad;
    private AppiumDriverLocalService service;
    private AppiumServer appiumServer= new AppiumServer();
    public DeviceData deviceData;
    private JSONObject deviceDetails;


    public Pixel3_XL(String device_a, String device_b, JSONObject readDeviceFile  ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
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
    public void run() {

        try {


            ad.findElement(By.id("com.google.android.apps.messaging:id/start_new_conversation_button")).click();
            ad.findElement(By.id("com.google.android.apps.messaging:id/recipient_text_view")).click();
            ad.getKeyboard().sendKeys(deviceB);
            Thread.sleep(1000);
//            ad.findElement(By.id("com.google.android.apps.messaging:id/contact_picker_create_group")).click();
            ad.findElement(By.id("com.google.android.apps.messaging:id/compose_message_text")).click();
            ad.findElement(By.id("com.google.android.apps.messaging:id/compose_message_text")).sendKeys("Hi mate! what is the time?");
            ad.findElement(By.id("com.google.android.apps.messaging:id/send_message_button_icon")).click();

            Date date = new Date();
            DateFormat format = new SimpleDateFormat("HHmm");
            String time = format.format(date);

            String message = " ["+deviceA+"] Hi mate! what is the time?)";
            //Message(senderDriver);
            System.out.println(message);
            String response = this.call(message);
            System.out.println(" ["+deviceA+"] Got '" + response + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    public void shutDown() throws IOException {
        deviceData.getAppiumDriver().quit();
        deviceData.getAppiumDriverLocalService().stop();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
