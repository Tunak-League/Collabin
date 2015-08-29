package tunakleague.com.redemption.notifications;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import tunakleague.com.redemption.MainActivity;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.messaging.DataProvider;
import tunakleague.com.redemption.messaging.DbHelper;

public class MessageListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String[] parsedMessage = message.split(" ", 2);
        String tag = parsedMessage[0];
        String sendMessage = parsedMessage[1];

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (tag.equals("MatchNotification")) {
            sendMatchNotification(sendMessage);
        }
        else {
            DbHelper dbHelper = new DbHelper(getApplication());
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            String sender = data.getString(DataProvider.COL_SENDER);
            String recipient = data.getString(DataProvider.COL_RECIPIENT);
            String time_sent = data.getString(DataProvider.COL_TIME_SENT);

            ContentValues values = new ContentValues();
            values.put(DataProvider.RECEIVED_OR_SENT, "received");
            values.put(DataProvider.COL_RECIPIENT, recipient);
            values.put(DataProvider.COL_SENDER, sender);
            values.put(DataProvider.COL_MESSAGE, sendMessage);
            values.put(DataProvider.COL_TIME_SENT, time_sent);

            // Insert the new row, returning the primary key value of the new row
            long id = db.insert(DbHelper.TABLE_NAME, null, values);
            Intent refreshChatLog = new Intent(NotificationsPreferences.INCOMING_CHAT_INSERTED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(refreshChatLog);
            sendNotification(sendMessage);
        }
    }

    private void sendNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gcm_cloud)
                .setContentTitle("New Message!")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, notificationBuilder.build());
    }

    /**
     * Create and show a simple notification containing the received Match notification.
     *
     * @param message GCM message received.
     */
    private void sendMatchNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_plusone_standard_off_client) // need something else here?
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentTitle("New Match!")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
