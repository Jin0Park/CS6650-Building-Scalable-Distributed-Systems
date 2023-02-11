package helper;

import part1.Part1Client;
import part2.Part2Client;

/**
 *  helper.Main class for Client package.
 */
public class Main {
    final static public int NUMTHREADS = 200;
    final static public int NUMREQUESTS = 500000;
    final static public String url = "http://34.219.173.79:8080/a1Servlet_war/twinder/";

    public static void main(String[] args) throws InterruptedException {
        Part1Client p1 = new Part1Client();
        p1.part1();
        Part2Client p2 = new Part2Client();
        p2.part2();
        System.out.println("Part1");
        p1.printResultPart1();
        System.out.println("Part2");
        p2.printResultPart2();

    }
}
