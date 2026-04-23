import java.util.LinkedList;

public class RCB {
    private int state; // represent available units (0, 1, 2, 3)
    private int inventory;
    private LinkedList<WaitingEntry> WaitingList;

    public RCB(int initInventory) {
        state = initInventory;
        inventory = initInventory;
        WaitingList = new LinkedList<>();
    }

    public void addProcessToWaitingList(int processID, int requestAmount) {
        WaitingEntry entry = new WaitingEntry(processID, requestAmount);
        WaitingList.add(entry);
    }

    public boolean hasWaitingProcess() {
        return !WaitingList.isEmpty();
    }

    public boolean isAvailable(int requestAmount) {
        return state >= requestAmount;
    }

    public boolean isValidRequest(int requestAmount) {
        return requestAmount > 0 && requestAmount <= inventory;
    }

    public LinkedList<WaitingEntry> getUnblockedProcesses() {
        LinkedList<WaitingEntry> unblockedProcesses= new LinkedList<>();
        while (!WaitingList.isEmpty() && state >= WaitingList.getFirst().getRequestAmount()) {
            WaitingEntry entry = WaitingList.removeFirst();
            setAllocated(entry.getRequestAmount()); // allocate resource to it
            unblockedProcesses.add(entry);
        }
        return unblockedProcesses;
    }

    // -------------------
    // Getters and Setters
    // -------------------

    public void setFree(int releaseAmount) {
        state += releaseAmount;
        if (state > inventory) {
            state = inventory;
        }
    }

    public void setAllocated(int requestAmount) {
        state -= requestAmount;
    }

    public int getInventory() {
        return inventory;
    }

    public int getAvailableUnits() {
        return state;
    }
}