package tunakleague.com.redemption.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import tunakleague.com.redemption.Constants;
import tunakleague.com.redemption.R;

public class IDRegistrationService extends IntentService {
    /**
     * This class serves 2 purposes:
     * 1. Obtaining the InstanceID token from the GCM server
     * 2. Sending this token to our REST server
     */
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public IDRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                /*Only send deviceID to server if token has been refreshed and needs to be updated on the server*/
                if( intent.getAction().equals(Constants.ACTION_REFRESH_ID))
                    Log.d(TAG, "refreshing"); //sendRegistrationToServer(token);
                else { //Triggers when intent Action is Constants.ACTION_CREATE_USER

                    /*Notify RegistrationActivity that deviceID was acquired successfully and pass it the deviceID */
                    Intent registrationComplete = new Intent( NotificationsPreferences.REGISTRATION_COMPLETE);
                    registrationComplete.putExtra(Constants.DEVICE_ID, token );
                    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                }

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(NotificationsPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(NotificationsPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.1.64:8000/hello/"; //localhost
        Log.d(TAG, "HEY WHAT'S THE DEAL");

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse( String response ) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String username = jsonResponse.getString("username"),
                                    deviceID = jsonResponse.getString("deviceID");
                            System.out.println("Username: "+username+"\ndeviceID: "+deviceID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) //end of function StringRequest. Since it's making a StringRequest object, you can conveniently add in extra class definition stuff like overrides
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("username", "justin");
                params.put("deviceID", token);
                return params;
            }
        };

        queue.add(postRequest);

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}