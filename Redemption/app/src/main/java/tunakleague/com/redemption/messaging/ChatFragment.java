package tunakleague.com.redemption.messaging;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.app_constants.ServerConstants;
import tunakleague.com.redemption.notifications.NotificationsPreferences;


public class ChatFragment extends Fragment  {
    private View view;
    private int toUser, sender;
    private EditText message;
    private Button btnSendMessage;
    private ListView listView;
    private ChatCursorAdapter adapter;
    private Cursor c;
    private BroadcastReceiver tokenBroadcastReceiver;
    private String result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_chat, container, false);
        toUser = getActivity().getIntent().getIntExtra("recipient", -1);
        sender = getActivity().getIntent().getIntExtra("sender", -1);
        listView = (ListView) view.findViewById(R.id.chatLog);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);
        tokenBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(NotificationsPreferences.INCOMING_CHAT_INSERTED)) {
                    return;
                }
                DbHelper dbHelper = new DbHelper(getActivity());
                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String[] projection = new String[]{
                        "_id",
                        DataProvider.RECEIVED_OR_SENT,
                        DataProvider.COL_MESSAGE,
                        DataProvider.COL_TIME_SENT
                };
                String whereClause = "(recipient = ? AND sender = ?) OR (recipient = ? AND sender = ?)";
                String whereArgs[] = new String[]{
                        Integer.toString(toUser),
                        Integer.toString(sender),
                        Integer.toString(sender),
                        Integer.toString(toUser)
                };
                String orderBy = DataProvider.COL_TIME_SENT + " ASC";

                c = db.query(
                        DbHelper.TABLE_NAME, // table name
                        projection, // columns to be returned by the query
                        whereClause, //
                        whereArgs,
                        null,
                        null,
                        orderBy
                );
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.changeCursor(c);
                    }
                });
                db.close();
            }
        };

        DbHelper dbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = new String[] {
                "_id",
                DataProvider.RECEIVED_OR_SENT,
                DataProvider.COL_MESSAGE,
                DataProvider.COL_TIME_SENT
        };
        String whereClause = "(recipient = ? AND sender = ?) OR (recipient = ? AND sender = ?)";
        String whereArgs[] = new String[] {
                Integer.toString(toUser),
                Integer.toString(sender),
                Integer.toString(sender),
                Integer.toString(toUser)
        };
        String orderBy = DataProvider.COL_TIME_SENT + " ASC";

        c = db.query(
                DbHelper.TABLE_NAME, // table name
                projection, // columns to be returned by the query
                whereClause, //
                whereArgs,
                null,
                null,
                orderBy
        );

        adapter = new ChatCursorAdapter(getActivity(), c, 2);

        listView.setAdapter(adapter);
        db.close();
        // to send message to another device via Google GCM
        btnSendMessage = (Button) view.findViewById(R.id.sendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                message = (EditText) view.findViewById(R.id.message);
                String messageToSend = message.getText().toString();
                message.setText("");

                if (TextUtils.isEmpty(messageToSend)) {
                    Toast.makeText(getActivity(), "Please enter a message.", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("ChatMainActivity", "Sending message to user: " + toUser);
                    sendMessageToGCMAppServer(Integer.toString(toUser), messageToSend);
                }
            }
        });
        return view;
    }

    private void sendMessageToGCMAppServer(final String recipient, final String messageToSend) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return sendMessage(recipient, messageToSend);
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    public final String sendMessage(final String toUserName, final String messageToSend) {
        String url = ServerConstants.URLS.CHAT.string;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            DbHelper dbHelper = new DbHelper(getActivity());
                            // Gets the data repository in write mode
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            // Create a new map of values, where column names are the keys
                            String msg = jsonResponse.getString(DataProvider.COL_MESSAGE);
                            String sender = jsonResponse.getString(DataProvider.COL_SENDER);
                            String recipient = jsonResponse.getString(DataProvider.COL_RECIPIENT);
                            String time_sent = jsonResponse.getString(DataProvider.COL_TIME_SENT);

                            ContentValues values = new ContentValues();
                            values.put(DataProvider.RECEIVED_OR_SENT, "sent");
                            values.put(DataProvider.COL_RECIPIENT, recipient);
                            values.put(DataProvider.COL_SENDER, sender);
                            values.put(DataProvider.COL_MESSAGE, msg);
                            values.put(DataProvider.COL_TIME_SENT, time_sent);

                            // Insert the new row, returning the primary key value of the new row
                            db.insert(DbHelper.TABLE_NAME, null, values);

                            db = dbHelper.getReadableDatabase();
                            String[] projection = new String[] {
                                    "_id",
                                    DataProvider.RECEIVED_OR_SENT,
                                    DataProvider.COL_MESSAGE,
                                    DataProvider.COL_TIME_SENT
                            };
                            String whereClause = "(recipient = ? AND sender = ?) OR (recipient = ? AND sender = ?)";
                            String whereArgs[] = new String[] {
                                    recipient,
                                    sender,
                                    sender,
                                    recipient
                            };
                            String orderBy = DataProvider.COL_TIME_SENT + " ASC";

                            c = db.query(
                                    DbHelper.TABLE_NAME, // table name
                                    projection, // columns to be returned by the query
                                    whereClause, //
                                    whereArgs,
                                    null,
                                    null,
                                    orderBy
                            );
                            adapter.changeCursor(c);
                            db.close();
                            result = jsonResponse.getString("recipient") + ": " + jsonResponse.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SENDMESSAGE", "ERROR");
                        error.printStackTrace();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put(Config.RECIPIENT, toUserName);
                paramsMap.put(Config.MESSAGE_KEY, messageToSend);
                return paramsMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PreferencesKeys.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        MyApplication.requestQueue.add(postRequest);
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter(NotificationsPreferences.INCOMING_CHAT_INSERTED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(tokenBroadcastReceiver, iff);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(tokenBroadcastReceiver);
    }

}
