import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import static com.squareup.okhttp.internal.Internal.logger;

public class CountLikesAndDislikes {
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> userData = new ConcurrentHashMap<>();
    private static final String EXCHANGENAME = "SWIPEEXCHANGE";
    private static final String LIKEKEY = "like";
    private static final String DISLIKEKEY = "dislike";
    private static final String LIKEDIR = "right";
    private static final int NUMCHANNELS = 50;
    private static final String QUEUENAME = "LIKE";
    private static Channel channel;

    public static void receive(String ip, int port, int threadNum) throws IOException, TimeoutException, InterruptedException {
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
                        addToMap(info);
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
        System.out.println("INFO: Queue based pool finished");
    }

    public static void addToMap(String[] messages) {
        String choice = messages[0];
        String swiperID = messages[1];
        boolean like = choice.equalsIgnoreCase(LIKEDIR);
        if (!userData.containsKey(swiperID)) {
            userData.put(swiperID, ConcurrentHMap.createMap());
        }
        if (like) {
            ConcurrentHMap.incrementCount(userData.get(swiperID), LIKEKEY);
        } else {
            ConcurrentHMap.incrementCount(userData.get(swiperID), DISLIKEKEY);
        }
    }
}
