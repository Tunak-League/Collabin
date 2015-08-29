package tunakleague.com.redemption;


public class ProjectChatRow {
    private String username;
    private int profileId;
    private String projectName;
    private int ownerId;
    private int projectId;

    public ProjectChatRow(String username, int profileId, String projectName, int ownerId, int projectId) {
        this.username = username;
        this.profileId = profileId;
        this.projectName = projectName;
        this.ownerId = ownerId;
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return username + " (Project: " + projectName + ")" + "\nTap to message";
    }

    public String getUsername() {
        return username;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getProjectId() {
        return projectId;
    }
}
