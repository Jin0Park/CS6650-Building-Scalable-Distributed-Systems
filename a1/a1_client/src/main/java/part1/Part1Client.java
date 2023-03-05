package part1;
import helper.*;
import io.swagger.client.*;
import io.swagger.client.api.SwipeApi;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
/**
 *  part1.Part1Client uses multithreaded method and calls part1.Part1ThreadConsumer. Also prints out the performance of part 1.
 */
public class Part1Client {
    private AtomicInteger count = new AtomicInteger(0);
    private static String[] resultPart1 = new String[5];
    synchronized public void inc() {
        this.count.getAndIncrement();
    }
    public AtomicInteger getVal() {
        return this.count;
    }

    public static void part1() throws InterruptedException {
        final Part1Client counter = new Part1Client();
        CountDownLatch completed = new CountDownLatch(Main.NUMTHREADS);
        long start = System.currentTimeMillis();
        int numOfRequests = Main.NUMREQUESTS / Main.NUMTHREADS;
        int leftoverRequests = Main.NUMREQUESTS % Main.NUMTHREADS;
        for (int i = 0; i < Main.NUMTHREADS; i++) {
            ApiClient client = new ApiClient();
            SwipeApi apiInstance = new SwipeApi(client);
            client.setBasePath(Main.url);

            int finalNumOfReq;
            if (i == Main.NUMTHREADS - 1) {
                finalNumOfReq = numOfRequests + leftoverRequests;
            } else {
                finalNumOfReq = numOfRequests;
            }

            Runnable thread = () -> {
                Part1ThreadConsumer consumer = new Part1ThreadConsumer(counter, apiInstance);
                consumer.run(finalNumOfReq);
                completed.countDown();
            };
            new Thread(thread).start();
        }
        completed.await();
        long end = System.currentTimeMillis();
        double timeTakenInSec = (double) (end - start) / 1000;
        double throughput = Main.NUMREQUESTS / timeTakenInSec;

        resultPart1[0] = "Number of successful requests sent is  " + counter.getVal();
        resultPart1[1] = "Number of unsuccessful requests is  " + (Main.NUMREQUESTS - counter.getVal().intValue());
        resultPart1[2] = "The total run time (wall time) is  " + timeTakenInSec;
        resultPart1[3] = "The total throughput in requests per second is  " + throughput;
        //resultPart1[4] = "Little's law prediction is " + (helper.Main.NUMTHREADS / (timeTakenInSec / helper.Main.NUMREQUESTS)) * 100;
    }

    public void printResultPart1() {
        for (int i = 0; i < resultPart1.length-1; i++) {
            System.out.println(resultPart1[i]);
        }
    }
}
