package tunakleague.com.redemption.authentication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.Constants;
import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants.*;
import tunakleague.com.redemption.notifications.IDRegistrationService;
import tunakleague.com.redemption.notifications.NotificationsPreferences;

public class RegistrationActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "RegistrationActivity";
    private BroadcastReceiver tokenBroadcastReceiver; //listens for REGISTRATION_COMPLETE message from IDRegistrationService

    //Input fields for creating new User
    private EditText username;
    private EditText password;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Assign input fields to instance variables
        username = (EditText) findViewById(R.id.user_input);
        password = (EditText) findViewById(R.id.password_input);
        email = (EditText) findViewById(R.id.email_input);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     *Submit the fields filled in by the user to the server to create a new user
     */
    public void registerUser(View view){
        Log.d(TAG, URLS.ROOT.string );
        /*User-entered inputs for username and password fields. Needed for login after registration*/
        final String username_input = username.getText().toString();
        final String password_input = password.getText().toString();

        tokenBroadcastReceiver = new BroadcastReceiver() { //Wait for IDRegistrationService to send you the deviceID from GCM

            /*
                Sends POST request to app server using the inputted fields and the deviceID returned by IDRegistrationIntent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!intent.getAction().equals(NotificationsPreferences.REGISTRATION_COMPLETE)) {
                    Log.d(TAG, "GET OUT");
                    return;

                }
                LocalBroadcastManager.getInstance(context).unregisterReceiver(tokenBroadcastReceiver); //Set this receiver to look for the REGISTRATION_COMPLETE broadcast

                final String deviceID = intent.getExtras().getString(Constants.DEVICE_ID); //Device id obtained from GCM
                String url = URLS.USER_LIST.string;

                /* Create the user-list POST request*/
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    String username = jsonResponse.getString("username");
                                    System.out.println("Created Username: "+username+"\ndeviceID: ");

                                    /*Automatically login after registration using registered username and password*/
                                    Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    loginIntent.setAction(Constants.ACTION_LOGIN);
                                    loginIntent.putExtra(USERS.USERNAME.string, username_input);
                                    loginIntent.putExtra(USERS.PASSWORD.string, password_input );
                                    startActivity(loginIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        },
                        new DetailedErrorListener(RegistrationActivity.this)
                ) //end of function StringRequest. Since it's making a StringRequest object, you can conveniently add in extra class definition stuff like overrides
                {
                    @Override
                    //Create the body of the request
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<>();

                        Log.d(TAG, username.getText().toString());
                        Log.d(TAG, password.getText().toString());
                        Log.d(TAG, email.getText().toString());
                        // Get the registration info from input fields and add them to the body of the request
                        params.put(USERS.USERNAME.string, username_input );
                        params.put(USERS.PASSWORD.string, password_input );
                        params.put(USERS.EMAIL.string, email.getText().toString() );
                        params.put(USERS.DEVICE_ID.string, deviceID);
                        params.put("Content-Type","application/json");
                        return params;
                    }
                };
                MyApplication.requestQueue.add(postRequest); //Add the request to the requestQueue so it can be sent to the app server.
                Log.d(TAG, "HOW MANY TIMES");
            }

        };

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenBroadcastReceiver,
                new IntentFilter(NotificationsPreferences.REGISTRATION_COMPLETE)); //Set this receiver to look for the REGISTRATION_COMPLETE broadcast


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM and obtain deviceID
            Intent intent = new Intent(this, IDRegistrationService.class);
            intent.setAction(Constants.ACTION_CREATE_USER);
            startService(intent);
        }


        Log.d(TAG, "Is it null?:" + String.valueOf(MyApplication.requestQueue == null));

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
