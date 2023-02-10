public class Output {
    long startTime;
    String requestType = "post";
    long latency;
    int responseCode;

    public long getStartTime() {
        return startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public long getLatency() {
        return latency;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String[] getData() {
        return new String[]{String.valueOf(getStartTime()), getRequestType(), String.valueOf(getLatency()), String.valueOf(getResponseCode())};
    }
    public String printData() {
        String[] data = getData();
        String result = "";
        for (int i = 0; i < 4; i ++) {
            result += data[i] + ",";
        }
        return result;
    }
}
