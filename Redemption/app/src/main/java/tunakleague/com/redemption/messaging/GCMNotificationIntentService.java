package tunakleague.com.redemption.messaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import tunakleague.com.redemption.MainActivity;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}
//	DbHelper dbHelper = new DbHelper(getApplication());
	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String msg = intent.getStringExtra(DataProvider.COL_MESSAGE);
				String sender = intent.getStringExtra(DataProvider.COL_SENDER);
                String recipient = intent.getStringExtra(DataProvider.COL_RECIPIENT);

		//		SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues vals = new ContentValues();
				vals.put(DataProvider.COL_MESSAGE, msg);
				vals.put(DataProvider.COL_SENDER, sender);
                vals.put(DataProvider.COL_RECIPIENT, recipient);

				//long id = db.insert(DataProvider.TABLE_CHAT, null, vals);

				sendNotification("Sender: "
						+ extras.get(ServerConstants.USERS.USERNAME.string) + ". Message: "
						+ extras.get(Config.MESSAGE_KEY));
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gcm_cloud)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}
}
