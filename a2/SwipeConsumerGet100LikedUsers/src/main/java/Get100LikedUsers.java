import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import static com.squareup.okhttp.internal.Internal.logger;

public class Get100LikedUsers {
    private static ConcurrentHashMap<String, ConcurrentLinkedQueue> userData = new ConcurrentHashMap<>();
    private static final String EXCHANGENAME = "SWIPEEXCHANGE";
    private static final String LIKEDIR = "right";
    private static final int NUMCHANNELS = 50;
    private static final String QUEUENAME = "100Users";
    private static Channel channel;

    public static void receive(String ip, int port, int threadNum) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("user1");
        factory.setPassword("pass1");
        factory.setVirtualHost("v1");
        factory.setHost(ip);
        factory.setPort(port);

        final Connection connection = factory.newConnection();
        RMQChannelFactory cf = new RMQChannelFactory(connection);
        RMQChannelPool pool = new RMQChannelPool(NUMCHANNELS, cf);

        for (int i = 0; i < threadNum; i++) {
            Runnable runnable = () -> {
                try {
                    channel = pool.borrowObject();
                    //channel = connection.createChannel();
                    channel.queueBind(QUEUENAME, EXCHANGENAME, "");
                    channel.basicQos(1);

                    final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        String[] info = message.split(",");
                        String choice = info[0];
                        if (choice.equalsIgnoreCase(LIKEDIR)) {
                            addToMap(info);
                        }
                        //System.out.println(" [x] Received '" + message + "'");
                    };
                    channel.basicConsume(QUEUENAME, false, deliverCallback, consumerTag -> {});
                    pool.returnObject(channel);
                } catch (IOException e) {
                    logger.info(e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            new Thread(runnable).start();
        }
        System.out.println("INFO: Queue based pool test finished");
    }

    public static void addToMap(String[] messages) {
        String swiperID = messages[1];
        String swipeeID = messages[2];
        if (!userData.containsKey(swiperID)) {
            userData.put(swiperID, ConcurrentQueue.createQueue());
        }
        ConcurrentQueue.addToQueue(userData.get(swiperID), swipeeID);
    }
}
