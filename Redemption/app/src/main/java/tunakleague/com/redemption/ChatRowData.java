package tunakleague.com.redemption;

public class ChatRowData {
    private String ownerName;
    private int ownerId;
    private String projectName;

    public ChatRowData(String ownerName, int ownerId, String projectName) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return projectName + "\nTap to message the owner (" + ownerName + ")";
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getProjectName() {
        return projectName;
    }
}
