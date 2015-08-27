package tunakleague.com.redemption.authentication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.notifications.NotificationsPreferences;


public class DeviceIDReceiver extends BroadcastReceiver {
    private String deviceID;
    @Override
    public void onReceive(Context context, Intent intent) {
        /*Abort and do nothing if the intent's action is not REGISTRATION_COMPLETE */
        if(!intent.getAction().equals(NotificationsPreferences.REGISTRATION_COMPLETE)) {
            return;

        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this); //unregister this receiver once the deviceID has been obtained

        deviceID = intent.getExtras().getString(Constants.DEVICE_ID); //Device id obtained from GCM
    }

    /*Return the obtained device ID. Will be null if called before onReceive() has finished executing*/
    public String getDeviceID() {
        return deviceID;
    }
}
