package part1;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import helper.*;

/**
 *  part1.Part1ThreadConsumer generates random values for swipe, swiperID, swipeeID, comment. Also, receives response code
 *  from the server.
 *  The methods that each thread will perform for part1.
 */
public class Part1ThreadConsumer {
    protected static final int SWIPERIDLIMIT = 5000;
    protected static final int SWIPEEIDLIMIT = 1000000;
    protected static final int COMMENTLIMIT = 256;
//    protected static final int SWIPERIDLIMIT = 5;
//    protected static final int SWIPEEIDLIMIT = 5;
//    protected static final int COMMENTLIMIT = 25;
    protected static final int RETRY = 5;
    protected Part1Client counter;
    protected SwipeApi apiInstance;

    public Part1ThreadConsumer(Part1Client counter, SwipeApi apiInstance) {
        this.counter = counter;
        this.apiInstance = apiInstance;
    }

    final static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    public void run(int numOfReq){
        for (int j = 0; j < numOfReq; j++) {
            SwipeDetails body = new SwipeDetails();

            String leftOrRight = randomDataGenerator.randomLeftOrRightGenerator();
            body.setSwiper(randomDataGenerator.randomNumGenerator(1, SWIPERIDLIMIT));
            body.setSwipee(randomDataGenerator.randomNumGenerator(1, SWIPEEIDLIMIT));
            body.setComment(randomDataGenerator.randomStringGenerator(COMMENTLIMIT));

            getResponsePart1(body, leftOrRight);
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
}