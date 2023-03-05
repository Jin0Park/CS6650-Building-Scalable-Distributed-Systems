public class Main {
    private static final int PORT = 5672;
    private static final String IP = "18.236.150.162";
    private static final int NUMTHREADS = 150;

    public static void main(String[] argv) throws Exception {
        Get100LikedUsers consumer = new Get100LikedUsers();
        consumer.receive(IP, PORT, NUMTHREADS);
    }
}
