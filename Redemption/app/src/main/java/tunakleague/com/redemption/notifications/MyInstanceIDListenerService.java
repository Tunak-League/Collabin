package tunakleague.com.redemption.notifications;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import tunakleague.com.redemption.app_constants.Constants;

public class MyInstanceIDListenerService extends InstanceIDListenerService {
    /**
     * This class checks the registration token (InstanceID token) and calls onTokenRefresh if it
     * needs to be refreshed and resent to the server.
     */
    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, IDRegistrationService.class);
        intent.setAction(Constants.ACTION_REFRESH_ID);
        startService(intent);
    }
    // [END refresh_token]
}