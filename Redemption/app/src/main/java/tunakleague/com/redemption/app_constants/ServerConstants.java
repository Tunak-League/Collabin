package tunakleague.com.redemption.app_constants;


public class ServerConstants {
    /*Constants related to the app server*/

    public enum URLS {
        /*Names of server endpoints */
        ROOT("https://tunak-server.herokuapp.com/"),
        //ROOT("http://209.52.104.91:8000/"),
        USER_LIST( ROOT.string + "user-list/"),
        USER_DETAIL( ROOT.string + "user-detail/" ),
        PROJECT_LIST( ROOT.string + "project-list/" ),
        PROJECT_DETAIL(ROOT.string + "project-detail/"),
        TOKEN_AUTH(ROOT.string + "api-token-auth/"),
        CHAT(ROOT.string + "chat/"),
        PROJECTS_MATCHES(ROOT.string + "project-matches/"),
        USER_GET(ROOT.string + "user-get/"),
	    USER_MATCHES(ROOT.string + "user-matches/"),
        USER_SEARCH( ROOT.string + "user-search/" ),
        PROJECT_SEARCH( ROOT.string + "project-search/" ),
        SKILLS(ROOT.string + "skills/" ),
        USER_SWIPE(ROOT.string + "user-swipe/"),
        PROJECT_SWIPE(ROOT.string + "project-swipe/"),
	    ;
        public final String string;
        private URLS(final String text ){
            this.string=text;
        }
    }


        /*Constants for the server's database tables and their fields*/

        public enum USERS {
            PK("id"),
            USERNAME("username"),
            PASSWORD("password"),
            EMAIL("email"),
            LOCATION("location"),
            USER_SUMMARY("user_summary"),
            DEVICE_ID("device_id"),
            TOKEN("token"),
            SKILLS("skills"),
            TYPES("types"),
            USER_IMAGE("user_image")

            ;
            public final String string;
            private USERS(final String text){
                this.string=text;
            }

        }

        public enum PROJECTS {
            PK("id"),
            PROJECT_NAME("project_name"),
            PROJECT_SUMMARY("project_summary"),
            SKILLS("skills"),
            TYPES("types"),
            PROJECT_IMAGE("project_image")
            ;
            public final String string;
            private PROJECTS(final String text){
                this.string=text;
            }

        }

    public enum SWIPES {
        USER_LIKES("user_likes"),
        PROJECT_LIKES("project_likes"),
        USER_PROFILE("user_profile"),
        PROJECT("project")
        ;
        public final String string;
        private SWIPES(final String text){
            this.string=text;
        }

    }


}

