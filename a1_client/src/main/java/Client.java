import io.swagger.client.*;
import io.swagger.client.api.SwipeApi;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    final static private int NUMTHREADS = 200;
    final static private int NUMREQUESTS = 500000;
    final static private String url = "http://35.86.176.121:8080/a1Servlet_war/twinder/";

    private AtomicInteger count = new AtomicInteger(0);
    private static String[] resultPart1 = new String[5];
    private static String[] resultPart2 = new String[5];
    synchronized public void inc() {
        this.count.getAndIncrement();
    }
    public AtomicInteger getVal() {
        return this.count;
    }
    public static ArrayList<Output> latencyRecords = new ArrayList<>();

    public static void part1() throws InterruptedException {
        final Client counter = new Client();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        long start = System.currentTimeMillis();
        int numOfRequests = NUMREQUESTS / NUMTHREADS;
        int leftoverRequests = NUMREQUESTS % NUMTHREADS;
        for (int i = 0; i < NUMTHREADS; i++) {
            ApiClient client = new ApiClient();
            SwipeApi apiInstance = new SwipeApi(client);
            client.setBasePath(url);

            int finalNumOfReq;
            if (i == NUMTHREADS - 1) {
                finalNumOfReq = numOfRequests + leftoverRequests;
            } else {
                finalNumOfReq = numOfRequests;
            }

            Runnable thread = () -> {
                ThreadConsumer consumer = new ThreadConsumer(counter, apiInstance);
                consumer.run(latencyRecords, finalNumOfReq, true);
                completed.countDown();
            };
            new Thread(thread).start();
        }
        completed.await();
        long end = System.currentTimeMillis();
        double timeTakenInSec = (double) (end - start) / 1000;
        double throughput = NUMREQUESTS / timeTakenInSec;

        resultPart1[0] = "Number of successful requests sent is  " + counter.getVal();
        resultPart1[1] = "Number of unsuccessful requests is  " + (NUMREQUESTS - counter.getVal().intValue());
        resultPart1[2] = "The total run time (wall time) is  " + timeTakenInSec;
        resultPart1[3] = "The total throughput in requests per second is  " + throughput;
        resultPart1[4] = "Little's law prediction is " + (NUMTHREADS / (timeTakenInSec / NUMREQUESTS)) * 100;
    }

    public static void part2() throws InterruptedException {
        final Client counter = new Client();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        long start = System.currentTimeMillis();

        int numOfRequests = NUMREQUESTS / NUMTHREADS;
        int leftoverRequests = NUMREQUESTS % NUMTHREADS;
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
                consumer.run(latencyRecords, finalNumOfReq, false);
                completed.countDown();
            };
            new Thread(thread).start();
        }
        completed.await();
        long end = System.currentTimeMillis();
        double timeTakenInSec = (double) (end - start) / 1000;
        double throughput = NUMREQUESTS / timeTakenInSec;
        WriteAndAnalyze wa = new WriteAndAnalyze(latencyRecords, NUMREQUESTS);
        wa.writeData("/Users/dominicjin/Desktop/csv/file3.csv");
        wa.analyze();

        resultPart2[0] = "Mean response time is " + wa.getMeanResTime();
        resultPart2[1] = "Median response time is " + wa.getMedianResTime();
        resultPart2[2] = "Throughput is " + throughput;
        resultPart2[3] = "p99 is " + wa.get99Percentile();
        resultPart2[4] = "Minimum and Maximum response times are " + wa.getMinResTime() + ", " + wa.getMaxResTime();
    }
    public static void main(String[] args) throws InterruptedException {
        part1();
        part2();
        for (int i = 0; i < resultPart1.length; i++) {
            System.out.println(resultPart1[i]);
        }
        for (int i = 0; i < resultPart2.length; i++) {
            System.out.println(resultPart2[i]);
        }
    }
}
