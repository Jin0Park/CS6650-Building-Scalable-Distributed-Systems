import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentQueue {
    private final static int LIMIT = 100;

    public static ConcurrentLinkedQueue<String> createQueue() {
        return new ConcurrentLinkedQueue<String>();
    }

    public static void addToQueue(ConcurrentLinkedQueue<String> queue, String userID) {
        if (queue.size() >= LIMIT) {
            queue.poll();
        }
        queue.add(userID);
    }


}
