import java.util.LinkedList;

public class RL {
    private LinkedList<Integer> priority0; // lowest priority
    private LinkedList<Integer> priority1;
    private LinkedList<Integer> priority2; // highest priority

    public RL() {
        priority0 = new LinkedList<>();
        priority1 = new LinkedList<>();
        priority2 = new LinkedList<>();
    }

    public void addProcess(int processID, int priority) {
        if (priority == 0) {
            priority0.add(processID);
        } else if (priority == 1) {
            priority1.add(processID);
        } else if (priority == 2) {
            priority2.add(processID);
        }
    }

    public void removeProcess(int processID, int priority) {
        if (priority == 0) {
            priority0.remove((Integer) processID);
        } else if (priority == 1) {
            priority1.remove((Integer) processID);
        } else if (priority == 2) {
            priority2.remove((Integer) processID);
        }
    }

    public void moveToEnd(int processID, int priority) {
        removeProcess(processID, priority);
        addProcess(processID, priority);
    }

    public int getNextProcess() {
        if (!priority2.isEmpty()) {
            return priority2.getFirst();
        } else if (!priority1.isEmpty()) {
            return priority1.getFirst();
        } else if (!priority0.isEmpty()) {
            return priority0.getFirst();
        }
        return -1; // no ready process
    }
}