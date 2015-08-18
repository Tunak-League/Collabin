package tunakleague.com.redemption;


public class ProjectChatRow {
    private String username;
    private int profileId;
    private String projectName;

    public ProjectChatRow(String username, int profileId, String projectName) {
        this.username = username;
        this.profileId = profileId;
        this.projectName = projectName;
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
}
