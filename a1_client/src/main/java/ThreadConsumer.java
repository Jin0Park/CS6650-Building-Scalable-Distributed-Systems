import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.util.ArrayList;

public class ThreadConsumer {
    protected static final int SWIPERIDLIMIT = 5000;
    protected static final int SWIPEEIDLIMIT = 1000000;
    protected static final int COMMENTLIMIT = 256;
    protected static final int RETRY = 5;
    protected Client counter;
    protected SwipeApi apiInstance;
    long totalResTime = 0;
    long medianResTime = 0;
    long minResTime = (long) Double.POSITIVE_INFINITY;
    long maxResTime = (long) Double.NEGATIVE_INFINITY;

    public ThreadConsumer(Client counter, SwipeApi apiInstance) {
        this.counter = counter;
        this.apiInstance = apiInstance;
    }

    final static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    public void run(ArrayList<Output> latencyRecords, int numOfReq){
        for (int j = 0; j < numOfReq; j++) {
            Output output = new Output();

            String leftOrRight = randomDataGenerator.randomLeftOrRightGenerator();

            SwipeDetails body = new SwipeDetails();
            body.setSwiper(randomDataGenerator.randomNumGenerator(1, SWIPERIDLIMIT));
            body.setSwipee(randomDataGenerator.randomNumGenerator(1, SWIPEEIDLIMIT));
            body.setComment(randomDataGenerator.randomStringGenerator(COMMENTLIMIT));


            getResponse(latencyRecords, body, leftOrRight, output);

        }
    }

    public void getResponse(ArrayList<Output> latencyRecords, SwipeDetails body, String leftOrRight, Output output){
        long startTime = System.currentTimeMillis();
        output.setStartTime(startTime);
        for (int k = 0; k < RETRY; k++) {
            try {
                ApiResponse r = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                output.setLatency(System.currentTimeMillis() - output.getStartTime());
                if (r.getStatusCode() == 201 || r.getStatusCode() == 200) {
                    int listSize = latencyRecords.size();
                    output.setResponseCode(r.getStatusCode());
                    latencyRecords.add(output);
                    if (listSize < latencyRecords.size()) {
                        counter.inc();
                    }
                    //System.out.println(counter.getVal() + "," + latencyRecords.size() + "," + output.printData());
                    break;
                }
            } catch (ApiException e) {
                output.setResponseCode(e.getCode());
                System.err.println(e.getCode());
                System.err.println(e.getResponseBody());
                e.printStackTrace();
            }
        }
    }
}