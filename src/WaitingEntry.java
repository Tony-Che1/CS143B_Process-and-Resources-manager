public class WaitingEntry {
    private int processID;
    private int requestAmount;

    public WaitingEntry(int processID, int requestAmount) {
        this.processID = processID;
        this.requestAmount = requestAmount;
    }

    // ------------------
    // Getters and Setters
    // ------------------

    public int getProcessID() {
        return processID;
    }

    public int getRequestAmount() {
        return requestAmount;
    }
}