import java.util.LinkedList;

public class PCB {
    private int processID;
    private int state; // 0: ready, 1: blocked
    private int parentID;
    private LinkedList<Integer> children;
    private LinkedList<ResourceAllocatedEntry> resources;
    private int priority;

    public PCB(int processID,int priority) {
        this.processID = processID;
        state = 0;
        parentID = -1;
        children = new LinkedList<>();
        resources = new LinkedList<>();
        this.priority = priority;
    }

    public void releaseResource(int resourceID, int releaseAmount) {
        for (int i = 0; i < resources.size(); i++) {
            ResourceAllocatedEntry entry = resources.get(i);
            if (entry.getResourceID() == resourceID) {
                entry.releaseResource(releaseAmount);
                if (entry.isFullyReleased()) {
                    resources.remove(i);
                }
                return;
            }
        }
    }

    public boolean hasChild(int childID) {
        return children.contains(childID);
    }

    public void allocateResource(int resourceID, int requestAmount) {
        for (ResourceAllocatedEntry entry : resources) {
            if (entry.getResourceID() == resourceID) {
                entry.allocateResource(requestAmount);
                return;
            }
        }
        resources.add(new ResourceAllocatedEntry(resourceID, requestAmount));
    }

    public void addChild(int child) {
        children.add(child);
    }

    public void removeChild(int child) {
        children.remove((Integer) child);
    }

    public boolean hasEnoughResource(int resourceID, int amount) {
        for (ResourceAllocatedEntry entry : resources) {
            if (entry.getResourceID() == resourceID) {
                return entry.getAllocatedAmount() >= amount;
            }
        }
        return false;
    }

    // ------------------
    // Getters and Setters
    // ------------------

    public void setReady() {
        state = 0;
    }

    public void setBlocked() {
        state = 1;
    }

    public void setParent(int parentID) {
        this.parentID = parentID;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public LinkedList<Integer> getChildrenList() {
        return children;
    }

    public int getChildren() {
        return children.getFirst();
    }

    public int getParent() {
        return parentID != -1 ? parentID : -1;
    }

    public LinkedList<ResourceAllocatedEntry> getAllocatedResources() {
        return resources;
    }

    public int getPriority() {
        return priority;
    }

    public int getProcessID() {
        return processID;
    }

    public int getHeldUnits(int resourceID) {
        for (ResourceAllocatedEntry entry : resources) {
            if (entry.getResourceID() == resourceID) {
                return entry.getAllocatedAmount();
            }
        }
        return 0;
    }
}