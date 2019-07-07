package tutor1_1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class Sender {

    private final static String QUEUE_NAME = "t1";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel())
        {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            Student s = new Student(123456789 , "TestStudent");
            byte [] message = getByteArray(s);
            if (message == null)
                System.err.println("Serialization Error");
            else {
                channel.basicPublish("", QUEUE_NAME, null, message);
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }

    private static byte[] getByteArray(Student s) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(s);
            return out.toByteArray();
        } catch (IOException e){
            return null;
        }
    }
}
