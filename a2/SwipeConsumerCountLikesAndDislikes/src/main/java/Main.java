public class Main {
    private static final int PORT = 5672;
    private static final int NUMTHREADS = 150;
    private static final String IP = "18.236.150.162";

    public static void main(String[] argv) throws Exception {
        CountLikesAndDislikes consumer = new CountLikesAndDislikes();
        consumer.receive(IP, PORT, NUMTHREADS);
    }
}
