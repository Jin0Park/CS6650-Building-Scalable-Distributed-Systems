import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentHMap {
    private static final String LIKEKEY = "like";
    private static final String DISLIKEKEY = "dislike";
    public static ConcurrentHMap chm = new ConcurrentHMap();
    synchronized public void inc(AtomicInteger count) {
        count.getAndIncrement();
    }

    public static ConcurrentHashMap<String, AtomicInteger> createMap() {
        ConcurrentHashMap<String, AtomicInteger> likesAndDislikes = new ConcurrentHashMap<String, AtomicInteger>() {{
            put("like", new AtomicInteger(0));
            put("dislike", new AtomicInteger(0));
        }};
        return likesAndDislikes;
    }

    public static void incrementCount(ConcurrentHashMap<String, AtomicInteger> map, String choice) {
        if (choice.equalsIgnoreCase(LIKEKEY)) {
            chm.inc(map.get(LIKEKEY));
        } else {
            chm.inc(map.get(DISLIKEKEY));
        }
    }
}
