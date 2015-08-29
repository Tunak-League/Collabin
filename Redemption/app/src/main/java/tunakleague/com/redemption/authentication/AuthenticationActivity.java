package tunakleague.com.redemption.authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.notifications.IDRegistrationService;


/*This class contains common methods used by Registration and Login Activities for obtaining deviceID from GCM*/
public abstract class AuthenticationActivity extends AppCompatActivity{
    public static final String TAG = "AuthenticationActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /*Starts the IDRegistrationService if device has Google Play Services APK*/
    public void startIDRegistrationService(){
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM and obtain deviceID
            Intent intent = new Intent(this, IDRegistrationService.class);
            intent.setAction(Constants.ACTION_CREATE_USER);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}

