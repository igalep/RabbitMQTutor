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
import pom.EditMessage;
import pom.Messages;
import pom.NewChatPage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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
    private URL urlPixel3_XL;


    public Pixel3_XL(String device_a, String device_b, JSONObject readDeviceFile  ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        deviceA = device_a;
        deviceB = device_b;
        devicDetails = readDeviceFile;
        deviceDetails = getSubJSON(devicDetails, deviceA);
        try {
            service = appiumServer.initServer();
            urlPixel3_XL = appiumServer.getServerURL();

        } catch (FileNotFoundException e) {
            e.printStackTrace(); }
        deviceData = DriverManagerFactory.getDriverManager(deviceDetails.getString("os"), deviceA,urlPixel3_XL).getDriverInfo();
        deviceData.setAppiumDriverLocalService(service);
        ad = deviceData.getAppiumDriver();
    }

    @Override
    public void run() {

        try {
            sendMessage();
            sendMessage2();


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

    public void sendMessage() throws InterruptedException, IOException {

        Messages messages = new Messages(ad);
        NewChatPage newChatPage = messages.newChat();
        newChatPage.setSearchContact(deviceB);
        EditMessage editMessage = newChatPage.chooseContact();
        String message = "Hi mate! what is the time?";
        editMessage.addMessage(message);

         Date date = new Date();
         DateFormat format = new SimpleDateFormat("HHmm");
         String time = format.format(date);

        String messageRabbit = " ["+deviceA+"] "+message;
        System.out.println(messageRabbit);
        String response = this.call(messageRabbit);
        boolean correctness = (response.contains(time))?true:false;
        System.out.println(" ["+deviceA+"] Got "+deviceB+" response- (" + response + " ) , and the correctness is: "+correctness);
    }

        public void sendMessage2() throws IOException, InterruptedException {
            String message2 = " What are you doing today?";

            EditMessage editMessage2 = new EditMessage(ad);
            editMessage2.addMessage(message2);

            String messageRabbit2 = " ["+deviceA+"] "+message2;
            System.out.println(messageRabbit2);
            String response2 = this.call(messageRabbit2);
            boolean correctness2 = (response2.contains("working"))?true:false;
            System.out.println(" ["+deviceA+"] Got "+deviceB+" response- (" + response2 + " ) , and the correctness is: "+correctness2);

            Bridge.setStatus(Boolean.toString(correctness2));
        }
}
