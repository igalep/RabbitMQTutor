package tutor1_1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Reciever {

    private final static String QUEUE_NAME = "t1";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            byte[] message = delivery.getBody();
            Student student = (Student) getObject(message);
            if (student == null)
                System.err.println("Desrialization Error");
            else
                System.out.println(" [x] Received student obj : '" + student.getName() + "' is a name and '" + student.getId() + "' as id");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    private static Object getObject(byte[] message) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            ObjectInputStream inputStream = new ObjectInputStream(in);
            return inputStream.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
