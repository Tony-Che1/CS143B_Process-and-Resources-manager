import java.util.LinkedList;

public class Manager {
    private PCB runningProcess;
    private PCB[] PCBList;
    private RCB[] RCBList;
    private RL RL;

    public Manager() {
        init();
    }

    public void init() {
        PCBList = new PCB[16];
        RCBList = new RCB[]{
            new RCB(1),
            new RCB(1),
            new RCB(2),
            new RCB(3)
        };
        RL = new RL();
        runningProcess = new PCB(0, 0); // create initial process 0 with priority 0
        PCBList[0] = runningProcess;
        RL.addProcess(0, 0); // add process 0 to ready list with priority 0
    }

    public void create(int priority) {
        boolean created = false;
        for (int i = 0; i < 16; i++) {
            if (PCBList[i] == null) {
                PCBList[i] = new PCB(i, priority);
                PCBList[i].setReady();
                runningProcess.addChild(i);
                PCBList[i].setParent(runningProcess.getProcessID());
                RL.addProcess(i, priority);
                System.out.println("process " + i + " created.");
                created = true;
                break;
            }
        }
        if (!created) {
            System.out.println("process creation failed: max process limit reached.");
        }
        scheduler();
    }

    public void destroy(int processID) {
        // Destroy the process with processID and all its children recursively
        if (processID == runningProcess.getProcessID()) {
            System.out.println("destroy failed: cannot destroy the running process.");
            return;
        }
        if (processID < 0 || processID >= PCBList.length || PCBList[processID] == null) {
            System.out.println("destroy failed: invalid process ID.");
            return;
        }
        if (!isDescendant(runningProcess.getProcessID(), processID)) {
            System.out.println("destroy failed: process " + processID + "is not a descendant of the running process.");
            return;
        } 
        int processDestroyed = destroyChildrenProcesses(processID); // destroy all children processes recursively
        System.out.println(processDestroyed + " processes destroyed");
        scheduler(); // schedule after destroying process
    }

    public void request(int resourceID, int requestAmount) {
        // Check if the resource is available
        if (resourceID < 0 || resourceID >= RCBList.length) {
            System.out.println("request failed: invalid resource ID.");
            return;
        }
        if (requestAmount <= 0) {
            System.out.println("request failed: request amount must be positive.");
            return;
        }
        if (!RCBList[resourceID].isValidRequest(requestAmount)) {
            System.out.println("request failed: request exceeds resource inventory.");
            return;
        }
        if (RCBList[resourceID].isAvailable(requestAmount)) {
            RCBList[resourceID].setAllocated(requestAmount); // allocate resource
            runningProcess.allocateResource(resourceID, requestAmount); // add resource to process's allocated list
            System.out.println("resource " + resourceID + " allocated");
        } else {
            // Add to waiting list
            runningProcess.setBlocked(); // set process to blocked
            RL.removeProcess(runningProcess.getProcessID(), runningProcess.getPriority()); // remove it from ready list
            RCBList[resourceID].addProcessToWaitingList(runningProcess.getProcessID(), requestAmount); // add it to resource's waiting list
            System.out.println("process " + runningProcess.getProcessID() + " blocked");
            scheduler();
        }
    }

    public void release(int resourceID, int releasedAmount){
        // Release the resource and check if there are waiting processes
        if (resourceID < 0 || resourceID >= RCBList.length) {
            System.out.println("release failed: invalid resource ID.");
            return;
        }
        if (releasedAmount <= 0) {
            System.out.println("release failed: release amount must be positive.");
            return;
        }
        if (!runningProcess.hasEnoughResource(resourceID, releasedAmount)) {
            System.out.println("release failed: process does not hold enough of resource " + resourceID + ".");
            return;
        }
        runningProcess.releaseResource(resourceID, releasedAmount); // release resource from process's allocated list
        RCBList[resourceID].setFree(releasedAmount); // increase available units in RCB
        if (RCBList[resourceID].hasWaitingProcess()) {
            LinkedList<WaitingEntry> unblockedEntries = RCBList[resourceID].getUnblockedProcesses(); // get the first waiting process
            for (WaitingEntry entry : unblockedEntries) {
                unblockProcess(entry, resourceID); // unblock the process and allocate resource to it
            }
        }
        System.out.println("resource " + resourceID + " released");
        scheduler();
    }

    public void timeout() {
        // Move the running process to the end of its priority queue
        RL.moveToEnd(runningProcess.getProcessID(), runningProcess.getPriority());
        scheduler();
    }

    public void scheduler() {
        // Implement scheduling logic based on priority and ready list
        int nextProcessID = RL.getNextProcess();
        if (nextProcessID != -1) {
            runningProcess = PCBList[nextProcessID];
            System.out.println("process " + nextProcessID + " running.");
        } else {
            System.out.println("no ready process to run.");
        }
    }

    // Helper method to release all resources allocated to a process (used in destroy)
    private void releaseAllResources(int processID) {
        PCB process = PCBList[processID];
        LinkedList<ResourceAllocatedEntry> copy = new LinkedList<>(process.getAllocatedResources());
        for (ResourceAllocatedEntry entry : copy) {
            int resourceID = entry.getResourceID();
            int allocatedAmount = entry.getAllocatedAmount();
            releaseResourceByDestroy(resourceID, allocatedAmount); // release all allocated resources
        }
    }

    // Helper method to release all resources allocated to a process (used in releaseAllResources)
    private void releaseResourceByDestroy(int resourceID, int allocatedAmount) {
        RCBList[resourceID].setFree(allocatedAmount); // increase available units in RCB
        if (RCBList[resourceID].hasWaitingProcess()) {
            LinkedList<WaitingEntry> unblockedEntries = RCBList[resourceID].getUnblockedProcesses(); // get the first waiting process
            for (WaitingEntry entry : unblockedEntries) {
                unblockProcess(entry, resourceID); // unblock the process and allocate resource to it
            }
        }
    }

    // Helper method to unblock a process and allocate resource to it (used in release)
    private void unblockProcess(WaitingEntry entry, int resourceID) {
        int processID = entry.getProcessID();
        int requestAmount = entry.getRequestAmount();
        RL.addProcess(processID, PCBList[processID].getPriority()); // add it to ready list
        PCBList[processID].setReady(); // set it to ready
        PCBList[processID].allocateResource(resourceID, requestAmount); // allocate resource to it
    }

    private int destroyChildrenProcesses(int processID) {
        int count = 0;
        while (PCBList[processID].hasChild()) {
            int childID = PCBList[processID].getChildren();
            count += destroyChildrenProcesses(childID);
        }
        int parentID = PCBList[processID].getParent();
        if (parentID != -1) PCBList[parentID].removeChild(processID);
        RL.removeProcess(processID, PCBList[processID].getPriority());
        releaseAllResources(processID);
        PCBList[processID] = null;
        return count + 1;
    }

    private boolean isDescendant(int ancestorID, int targetID) {
        if (ancestorID == targetID) return true;
        PCB ancestor = PCBList[ancestorID];
        for (int childID : ancestor.getChildrenList()) {
            if (isDescendant(childID, targetID)) {
                return true;
            }
        }
        return false;
    }
}