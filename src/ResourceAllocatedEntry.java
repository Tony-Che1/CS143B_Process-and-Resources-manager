public class ResourceAllocatedEntry {
    private int resourceID;
    private int allocatedAmount;

    public ResourceAllocatedEntry(int resourceID, int allocatedAmount) {
        this.resourceID = resourceID;
        this.allocatedAmount = allocatedAmount;
    }

    public void releaseResource(int releaseAmount) {
        allocatedAmount -= releaseAmount;
    }

    public void allocateResource(int requestAmount) {
        allocatedAmount += requestAmount;
    }

    public boolean isFullyReleased() {
        return allocatedAmount <= 0;
    }

    // ------------------
    // Getters and Setters
    // ------------------

    public int getResourceID() {
        return resourceID;
    }
    
    public int getAllocatedAmount() {
        return allocatedAmount;
    }
}