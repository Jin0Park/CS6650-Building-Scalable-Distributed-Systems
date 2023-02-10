import java.security.SecureRandom;
import java.util.List;

public class RandomDataGenerator {
    static private SecureRandom rand = new SecureRandom();

    public String randomLeftOrRightGenerator() {
        int num = rand.nextInt();
        if (num % 2 == 0) {
            return "left";
        }
        return "right";
    }

    public String randomNumGenerator(int start, int end) {
        int num = rand.nextInt(end - start) + start + 1;
        return String.valueOf(num);
    }

    public String randomStringGenerator(int max) {
        int randomNum = rand.nextInt(max+1);
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        String generatedString = rand.ints(leftLimit, rightLimit + 1)
                .limit(randomNum)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
}
