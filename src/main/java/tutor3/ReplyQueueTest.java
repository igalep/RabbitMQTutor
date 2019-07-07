//package tutor3;
//
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import cucumber.api.java.After;
//import cucumber.api.java.Before;
//import org.openqa.selenium.TimeoutException;
//import org.springframework.amqp.core.ExchangeTypes;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static javax.xml.crypto.dsig.CanonicalizationMethod.EXCLUSIVE;
//
//public class ReplyQueueTest extends BaseConfig {
//
//    private static final String CLOSING_MESSAGE = "X";
//    private static final String EXCHANGE = "Reply_Queue_Test";
//    private static final String RECEIVER_QUEUE = "RPC_Receiver";
//    private static final String SENDER_QUEUE = "RPC_Sender";
//    private static final String REPLY_EXC_KEY = "Key_1";
//    private static final Map<String, Integer> STATS = new ConcurrentHashMap<>();
//
//    private Connection connection;
//
//    private Channel receiverChannel;
//
//    private Channel senderChannel;
//
//    @Before
//    public void initializeQueues() throws IOException, TimeoutException {
//        connection = getConnection();
//        receiverChannel = connection.createChannel();
//        receiverChannel.exchangeDeclare(EXCHANGE, ExchangeTypes.DIRECT.getName());
//        senderChannel = connection.createChannel();
//
//        receiverChannel.queueDeclare(RECEIVER_QUEUE, DURABLE.not(), EXCLUSIVE.not(), AUTO_DELETE.yes(),
//                Collections.emptyMap());
//        receiverChannel.basicQos(0); // maximum number of messages that server will deliver, 0 = unlimited
//        receiverChannel.queueBind(RECEIVER_QUEUE, EXCHANGE, REPLY_EXC_KEY);
//
//        senderChannel.queueDeclare(SENDER_QUEUE, DURABLE.not(), EXCLUSIVE.yes(), AUTO_DELETE.yes(),
//                Collections.emptyMap());
//    }
//
//    @After
//    public void clearQueues() throws IOException, TimeoutException {
//        STATS.clear();
//
//        /**
//         * Before deleting exchange, we send a message indicating
//         * that the {@code QueueingConsumer}s should stop to wait
//         * for next deliveries. Without that, an
//         * {@code ConsumerCancelledException} is thrown.
//         */
//        senderChannel.basicPublish("", RECEIVER_QUEUE, null,
//                CLOSING_MESSAGE.getBytes("UTF-8"));
//
//        receiverChannel.queueDelete(RECEIVER_QUEUE);
//        senderChannel.queueDelete(SENDER_QUEUE);
//        receiverChannel.exchangeDelete(EXCHANGE);
//        senderChannel.exchangeDelete(EXCHANGE);
//
//        receiverChannel.close();
//        senderChannel.close();
//        connection.close();
//    }
//
//    @Test
//    public void should_consume_and_reply_the_message() throws IOException, InterruptedException {
//        String message = "Hello world";
//        CountDownLatch latch = new CountDownLatch(2);
//
//        new Thread(new ReceiverListener(receiverChannel, latch)).start();
//        new Thread(new SenderListener(senderChannel, latch, message)).start();
//
//        latch.await(2, TimeUnit.SECONDS);
//
//        assertThat(STATS.get(message).intValue()).isEqualTo(1);
//        assertThat(STATS.get(constructResponse(message)).intValue()).isEqualTo(1);
//    }
//
//    private static class SenderListener implements Runnable {
//
//        private final QueueingConsumer consumer;
//        private final CountDownLatch latch;
//        private final String message;
//
//        private SenderListener(Channel channel, CountDownLatch latch, String message) {
//            this.consumer = new QueueingConsumer(channel);
//            try {
//                channel.basicConsume("", AUTO_ACK.yes(), consumer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            this.latch = latch;
//            this.message = message;
//        }
//
//        @Override
//        public void run() {
//            Channel senderChannel = consumer.getChannel();
//
//            try {
//                String corrId = UUID.randomUUID().toString();
//                AMQP.BasicProperties messageProps = new AMQP.BasicProperties
//                        .Builder()
//                        .correlationId(corrId)
//                        .replyTo(SENDER_QUEUE)
//                        .build();
//                System.out.println("["+ SENDER_QUEUE +"] I'm sending new message "+message +
//                        " with correlation id "+corrId);
//                senderChannel.basicPublish("", RECEIVER_QUEUE,
//                        messageProps, message.getBytes("UTF-8"));
//
//                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                String receivedMessage = new String(delivery.getBody(),"UTF-8");
//                System.out.println("["+ SENDER_QUEUE +"] Got response from ["+RECEIVER_QUEUE+"] : "+
//                        receivedMessage);
//                STATS.put(receivedMessage, 1);
//            } catch (IOException|InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            latch.countDown();
//        }
//    }
//
//    private static class ReceiverListener implements Runnable {
//
//        private final QueueingConsumer consumer;
//        private final CountDownLatch latch;
//
//        private ReceiverListener(Channel channel, CountDownLatch latch)  {
//            this.consumer = new QueueingConsumer(channel);
//            try {
//                channel.basicConsume(RECEIVER_QUEUE, AUTO_ACK.not(), consumer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            this.latch = latch;
//        }
//
//        @Override
//        public void run() {
//            Channel receiverChannel = consumer.getChannel();
//            try {
//                while (true) {
//                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                    AMQP.BasicProperties messageProps = delivery.getProperties();
//                    String message = new String(delivery.getBody(),"UTF-8");
//                    if (message.equals(CLOSING_MESSAGE)) {
//                        break;
//                    }
//                    STATS.put(message, 1);
//                    String response = constructResponse(message);
//                    System.out.println("[" + RECEIVER_QUEUE + "] Receive message " + message +
//                            " with correlationId " + messageProps.getCorrelationId());
//                    System.out.println("[" + RECEIVER_QUEUE + "] Publishing new message " +
//                            response);
//                    receiverChannel.basicPublish("", messageProps.getReplyTo(), messageProps,
//                            response.getBytes("UTF-8"));
//                    receiverChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                }
//
//            } catch (IOException|InterruptedException e) {
//                e.printStackTrace();
//            }
//            latch.countDown();
//        }
//    }
//
//    private static String constructResponse(String message) {
//        return "Re: "+message;
//    }
//
//}