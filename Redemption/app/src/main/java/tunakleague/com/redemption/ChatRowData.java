package tunakleague.com.redemption;

public class ChatRowData {
    private String ownerName;
    private int ownerId;
    private String projectName;
    private int userProfileId;
    private int projectId;

    public ChatRowData(String ownerName, int ownerId, String projectName, int userProfileId, int projectId) {
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.projectName = projectName;
        this.userProfileId = userProfileId;
        this.projectId = projectId;
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

    public int getUserProfileId() {
        return userProfileId;
    }

    public int getProjectId() {
        return projectId;
    }
}
