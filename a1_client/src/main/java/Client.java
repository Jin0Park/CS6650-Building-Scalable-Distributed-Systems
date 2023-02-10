import io.swagger.client.*;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    final static private int NUMTHREADS = 200;
    final static private int NUMREQUESTS = 500000;
    //final static private String url = "http://34.222.65.20:8080/a1Servlet_war/twinder/";
    final static private String url = "http://localhost:8080/a1Servlet_war/twinder/";

    private AtomicInteger count = new AtomicInteger(0);
    synchronized public void inc() {
        this.count.getAndIncrement();
    }
    public AtomicInteger getVal() {
        return this.count;
    }

    public static void main(String[] args) throws InterruptedException {
        final Client counter = new Client();
        ArrayList<Output> latencyRecords = new ArrayList<>();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        long start = System.currentTimeMillis();

        int numOfRequests = NUMREQUESTS / NUMTHREADS;
        int leftoverRequests = NUMREQUESTS % NUMTHREADS;
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        for (int i = 0; i < NUMTHREADS; i++) {
            ApiClient client = new ApiClient();
            SwipeApi apiInstance = new SwipeApi(client);
            client.setBasePath(url);

            int finalNumOfReq;
            if(i == NUMTHREADS - 1) {
                finalNumOfReq = numOfRequests + leftoverRequests;
            } else {
                finalNumOfReq = numOfRequests;
            }

            Runnable thread = () -> {
                ThreadConsumer consumer = new ThreadConsumer(counter, apiInstance);
                consumer.run(latencyRecords, finalNumOfReq);
                completed.countDown();

            };
            new Thread(thread).start();
        }
        completed.await();
        long end = System.currentTimeMillis();
        double timeTakenInSec = (double) (end - start) / 1000;

        System.out.println("Number of successful requests sent is  " + counter.getVal());
        System.out.println("Number of unsuccessful requests is  " + (NUMREQUESTS - counter.getVal().intValue()));
        System.out.println("The total run time (wall time) is  " + timeTakenInSec);
        System.out.println("The total throughput in requests per second is  " + (double) NUMREQUESTS / timeTakenInSec);
        WriteAndAnalyze wa = new WriteAndAnalyze(latencyRecords, NUMREQUESTS);
        System.out.println(latencyRecords.size());
        wa.writeData("/Users/dominicjin/Desktop/csv/file3.csv");

        System.out.println();
        wa.analyze();
        System.out.println("Mean response time is " + wa.getMeanResTime());
        System.out.println("Median response time is " + wa.getMedianResTime());
        System.out.println("Throughput is " + (double) NUMREQUESTS / timeTakenInSec);
        System.out.println("p99 is " + wa.get99Percentile());
        System.out.println("Minimum and Maximum response times are " + wa.getMinResTime() + ", " + wa.getMaxResTime());
    }
}
