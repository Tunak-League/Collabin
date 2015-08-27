package tunakleague.com.redemption.messaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants;

public class ChatMainActivity extends AppCompatActivity {
	private MessageSender appUtil;
	private String userName;

	private int toUser;
	private EditText message;
	private Button btnSendMessage;
	private ListView listView;
    private String username, first_name, last_name, email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appUtil = new MessageSender(this);
        toUser = getIntent().getIntExtra("recipient", -1);
        listView = (ListView) findViewById(R.id.chatLog);

        String url = ServerConstants.URLS.USER_GET.string + Integer.toString(toUser) + "/";
        StringRequest jsonRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            username = data.getString("username");
                            first_name = data.getString("first_name");
                            last_name = data.getString("last_name");
                            email = data.getString("email");
                            ActionBar actionBar = getSupportActionBar();
                            actionBar.setTitle(username + " (" + first_name + " " + last_name + ")");
                            actionBar.setSubtitle(email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(jsonRequest);

        //DbHelper dbHelper = new DbHelper(ChatMainActivity.this);
        //SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DataProvider.COL_MESSAGE,
        };
        //device = models.ForeignKey('push_notifications.GCMDevice', null = True, blank = True, default = None)Cursor c = db.rawQuery("SELECT message from " + dbHelper.getDatabaseName() + " WHERE " +
        //        DataProvider.COL_SENDER + " =? AND " + DataProvider.COL_RECIPIENT + " =?", new String[] {"", Integer.toString(toUser)});

        // ChatCursorAdapter adapter = new ChatCursorAdapter(this, c, 2);

        // listView.setAdapter(adapter);

		// to send message to another device via Google GCM
		btnSendMessage = (Button) findViewById(R.id.sendMessage);
		btnSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				message = (EditText) findViewById(R.id.message);
				String messageToSend = message.getText().toString();

				if (TextUtils.isEmpty(messageToSend)) {
					Toast.makeText(getApplicationContext(), "Please enter a message.", Toast.LENGTH_LONG).show();
				}
                else {
					Log.d("ChatMainActivity", "Sending message to user: " + toUser);
					sendMessageToGCMAppServer(Integer.toString(toUser), messageToSend);

				}
			}
		});
	}

	private void sendMessageToGCMAppServer(final String toUserName, final String messageToSend) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return appUtil.sendMessage(toUserName, messageToSend);
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d("ChatMainActivity", "Result: " + msg);
			}
		}.execute(null, null, null);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_user_chat, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_unmatch):
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
	}
}
