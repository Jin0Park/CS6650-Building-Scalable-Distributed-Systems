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

    public ThreadConsumer(Client counter, SwipeApi apiInstance) {
        this.counter = counter;
        this.apiInstance = apiInstance;
    }

    final static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    public void run(ArrayList<Output> latencyRecords, int numOfReq, boolean isPart1){
        for (int j = 0; j < numOfReq; j++) {
            Output output = new Output();
            SwipeDetails body = new SwipeDetails();

            String leftOrRight = randomDataGenerator.randomLeftOrRightGenerator();
            body.setSwiper(randomDataGenerator.randomNumGenerator(1, SWIPERIDLIMIT));
            body.setSwipee(randomDataGenerator.randomNumGenerator(1, SWIPEEIDLIMIT));
            body.setComment(randomDataGenerator.randomStringGenerator(COMMENTLIMIT));

            if (isPart1) {
                getResponsePart1(body, leftOrRight);
            } else {
                getResponsePart2(latencyRecords, body, leftOrRight, output);
            }
        }
    }
    public void getResponsePart1(SwipeDetails body, String leftOrRight){
        for (int k = 0; k < RETRY; k++) {
            try {
                ApiResponse r = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                if (r.getStatusCode() == 201 || r.getStatusCode() == 200) {
                    counter.inc();
                    break;
                }
            } catch (ApiException e) {
                System.err.println(e.getCode());
                System.err.println(e.getResponseBody());
                e.printStackTrace();
            }
        }
    }

    public void getResponsePart2(ArrayList<Output> latencyRecords, SwipeDetails body, String leftOrRight, Output output){
        long startTime = System.currentTimeMillis();
        output.setStartTime(startTime);
        for (int k = 0; k < RETRY; k++) {
            try {
                ApiResponse r = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                long endTime = System.currentTimeMillis();
                output.setLatency(endTime - startTime);
                if (r.getStatusCode() == 201 || r.getStatusCode() == 200) {
                    output.setResponseCode(r.getStatusCode());
                    latencyRecords.add(output);
                    counter.inc();
                    break;
                }
            } catch (ApiException e) {
                output.setResponseCode(e.getCode());
                System.err.println(e.getCode());
                System.err.println(e.getResponseBody());
                e.printStackTrace();
                latencyRecords.add(output);
            }
        }
    }
}