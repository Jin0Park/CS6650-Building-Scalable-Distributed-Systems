import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class WriteAndAnalyze {
    private ArrayList<Output> records;

    private long totalResTime = 0;
    private long medianResTime = 0;
    private long meanResTime = 0;
    private long minResTime = (long) Double.POSITIVE_INFINITY;
    private long maxResTime = (long) Double.NEGATIVE_INFINITY;
    private int numReq;
    private ArrayList<Long> latencyList = new ArrayList<>();
    public WriteAndAnalyze(ArrayList<Output> records, int numReq) {
        this.records = records;
        this.numReq = numReq;
    }

    public void analyze() {
        getSortedLatencyList();
        for (int i = 0; i < latencyList.size(); i++) {
            long currentResTime = latencyList.get(i);
            totalResTime += currentResTime;
        }
    }

    public void getSortedLatencyList() {
        for (int i = 0; i < this.records.size(); i++) {
            latencyList.add(this.records.get(i).getLatency());
        }
        latencyList.sort(Comparator.naturalOrder());
    }

    public double getMeanResTime() {
        return (double) totalResTime / this.numReq;
    }

    public long getTotalResTime() {
        return totalResTime;
    }

    public double getMedianResTime() {
        getSortedLatencyList();
        if (latencyList.size() % 2 == 0) {
            return (double) (latencyList.get(latencyList.size()/2) + latencyList.get(latencyList.size()/2 - 1))/2;
        } else {
            return (double) latencyList.get(latencyList.size() / 2);
        }
    }

    public double getMinResTime() {
        return (double) latencyList.get(0);
    }

    public double getMaxResTime() {
        return (double) latencyList.get(latencyList.size()-1);
    }

    public double get99Percentile() {
        int arrayIndex = (int) Math.round(latencyList.size() * 0.99 + .5) - 1;
        return (double) latencyList.get(arrayIndex);
    }

    public void writeData(String filePath) {
//        for (int k = 0; k < this.records.size(); k++) {
//            System.out.println(this.records.get(k).getStartTime());
//        }
        File file = new File(filePath);
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = {"start time", "request type", "latency", "response code"};

            writer.writeNext(header);

            int i = 0;
            while (i < this.records.size()) {
                String[] input = new String[5];
                input[0] = String.valueOf(i);
                input[1] = String.valueOf(this.records.get(i).getStartTime());
                input[2] = this.records.get(i).getRequestType();
                input[3] = String.valueOf(this.records.get(i).getLatency());
                input[4] = String.valueOf(this.records.get(i).getResponseCode());
//                for (int j = 0; j < 5; j++) {
//                    System.out.println(i + "," + input[1] + "," + input[2] + "," + input[3] + "," + input[4]);
//                }
                writer.writeNext(input);

                //writer.writeNext(this.records.get(i).getData());
                i++;
            }
            writer.close();
        }
        catch (Exception e) {
            System.err.println("Something is wrong");
            e.printStackTrace();
        }
    }

}
