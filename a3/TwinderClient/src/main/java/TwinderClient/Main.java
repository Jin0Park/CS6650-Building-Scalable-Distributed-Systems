package TwinderClient;
import TwinderClient.PostRequestClient.PostRequestClient;

import java.util.concurrent.CountDownLatch;

/**
 *  Main class for Client package.
 */
public class Main {
    final static public int NUMTHREADS = 150;
    final static public int NUMREQUESTS = 500000;
    final static public int POST_THREADS = NUMTHREADS - 1;
    final static public int GET_THREADS = 1;
    public static CountDownLatch completed = new CountDownLatch(POST_THREADS);
    final static public String url = "http://34.219.31.52:8080/SwipeServlet_war/twinders/";

    public static void main(String[] args) throws InterruptedException {
        PostRequestClient p1 = new PostRequestClient();
        p1.generatePostRequest();

        System.out.println("Result");
        p1.printResults();
    }
}
