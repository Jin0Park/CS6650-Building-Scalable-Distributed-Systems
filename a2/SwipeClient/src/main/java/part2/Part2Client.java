package part2;

import helper.*;
import io.swagger.client.*;
import io.swagger.client.api.SwipeApi;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *  part2.Part2Client uses multithreaded method and calls part1.Part1ThreadConsumer. Also prints out the performance of part 2.
 */
public class Part2Client {
    private AtomicInteger count = new AtomicInteger(0);
    private static String[] resultPart2 = new String[5];
    synchronized public void inc() {
        this.count.getAndIncrement();
    }
    public AtomicInteger getVal() {
        return this.count;
    }
    public static ArrayList<Output> latencyRecords = new ArrayList<>();


    public static void part2() throws InterruptedException {
        final Part2Client counter = new Part2Client();
        CountDownLatch completed = new CountDownLatch(Main.NUMTHREADS);
        long start = System.currentTimeMillis();

        int numOfRequests = Main.NUMREQUESTS / Main.NUMTHREADS;
        int leftoverRequests = Main.NUMREQUESTS % Main.NUMTHREADS;
        for (int i = 0; i < Main.NUMTHREADS; i++) {
            ApiClient client = new ApiClient();
            SwipeApi apiInstance = new SwipeApi(client);
            client.setBasePath(Main.url);

            int finalNumOfReq;
            if(i == Main.NUMTHREADS - 1) {
                finalNumOfReq = numOfRequests + leftoverRequests;
            } else {
                finalNumOfReq = numOfRequests;
            }

            Runnable thread = () -> {
                Part2ThreadConsumer consumer = new Part2ThreadConsumer(counter, apiInstance);
                consumer.run(latencyRecords, finalNumOfReq, false);
                completed.countDown();
            };
            new Thread(thread).start();
        }
        completed.await();
        long end = System.currentTimeMillis();
        double timeTakenInSec = (double) (end - start) / 1000;
        double throughput = Main.NUMREQUESTS / timeTakenInSec;
        WriteAndAnalyze wa = new WriteAndAnalyze(latencyRecords, Main.NUMREQUESTS);
        wa.writeData("/Users/dominicjin/Desktop/file3.csv");
        wa.analyze();
        System.out.println();
        resultPart2[0] = "Mean response time is " + wa.getMeanResTime();
        resultPart2[1] = "Median response time is " + wa.getMedianResTime();
        resultPart2[2] = "Throughput is " + throughput;
        resultPart2[3] = "p99 is " + wa.get99Percentile();
        resultPart2[4] = "Minimum and Maximum response times are " + wa.getMinResTime() + ", " + wa.getMaxResTime();
    }
    public static void printResultPart2() {
        for (int i = 0; i < resultPart2.length; i++) {
            System.out.println(resultPart2[i]);
        }
        System.out.println();
    }
}
