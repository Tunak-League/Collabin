package tunakleague.com.redemption;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.app_constants.PreferencesKeys;

public class MyApplication extends Application {
    public static RequestQueue requestQueue; //global queue for making http requests

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    /*
    Returns a map containing the Authentication header with the token for the user.
    @requires: SharedPreferences must contain a valid K-V pair
     */
    public static Map<String,String> getAuthenticationHeader( Context context ) {
        Map<String, String>  params = new HashMap<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        params.put("Authorization", "Token " + settings.getString(PreferencesKeys.AUTH_TOKEN, "noToken"));
        //TODO: Put in the token header
        return params;
    }


}
