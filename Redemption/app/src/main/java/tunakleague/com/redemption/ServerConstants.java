package tunakleague.com.redemption;


public class ServerConstants {
    /*Constants related to the app server*/

    public enum URLS {
        /*Names of server endpoints */
        ROOT("http://192.168.1.64:8000/"),
        USER_LIST( ROOT.string + "user-list/"),
        TOKEN_AUTH(ROOT.string + "api-token-auth/")

        ;
        public final String string;
        private URLS(final String text ){
            this.string=text;
        }



    }


        /*Constants for the server's database tables and their fields*/
        public enum USERS_TABLE{
            USERNAME("username"),
            PASSWORD("password"),
            EMAIL("email"),
            DEVICE_ID("device_id"),
            TOKEN("token"),
            ;
            public final String string;
            private USERS_TABLE(final String text ){
                this.string=text;
            }

        }


}

