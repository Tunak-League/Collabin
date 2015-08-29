package tunakleague.com.redemption.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


import tunakleague.com.redemption.app_constants.Constants;
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
                if (intent.getAction().equals(Constants.ACTION_REFRESH_ID))
                    Log.d(TAG, "refreshing"); //sendRegistrationToServer(token);
                else { //Triggers when intent Action is Constants.ACTION_CREATE_USER

                    /*Notify RegistrationActivity that deviceID was acquired successfully and pass it the deviceID */
                    Intent registrationComplete = new Intent(NotificationsPreferences.REGISTRATION_COMPLETE);
                    registrationComplete.putExtra(Constants.DEVICE_ID, token);
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