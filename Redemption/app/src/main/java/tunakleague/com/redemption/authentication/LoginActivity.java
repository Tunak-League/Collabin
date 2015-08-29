package tunakleague.com.redemption.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.HomeActivity;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants.*;
import tunakleague.com.redemption.notifications.NotificationsPreferences;

public class LoginActivity extends AuthenticationActivity {
    public final String TAG = "LoginActivity";

    EditText username;
    EditText password;

    String deviceID = Constants.NO_DEVICE; //If regular login (not from RegistrationActivity), used to check if user is on a different device. No device by default
    DeviceIDReceiver tokenBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username =  (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);

        Intent intent = getIntent();
        /*If LoginActivity was started from RegistrationActivity, retrieve passed username/password to login automatically*/
        if (intent.getAction() != null && intent.getAction().equals(Constants.ACTION_LOGIN)) {
            String username_input = intent.getExtras().getString(USERS.USERNAME.string);
            String password_input = intent.getExtras().getString(USERS.PASSWORD.string);
            Log.d(TAG, username_input);
            Log.d(TAG, password_input);
            deviceID =  PreferenceManager.getDefaultSharedPreferences(this).getString(PreferencesKeys.DEVICE_ID, Constants.NO_DEVICE); //Assign old ID to pass the check for different device, since this code occurs right after RegistrationActivity
            authenticate(username_input, password_input, false);
        }
        /*Manual login. Must retrieve new device ID from GCM in case user has switched devices.*/
        else {
        /*Obtain device ID from GCM so we can check it against the stored ID in preferences and see if user is on a different device.*/
            tokenBroadCastReceiver = new DeviceIDReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent); //call parent method to obtain the deviceID
                    deviceID = getDeviceID();
                    Log.d(TAG, "Device ID: " + deviceID);
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(tokenBroadCastReceiver,
                    new IntentFilter(NotificationsPreferences.REGISTRATION_COMPLETE)); //Set this receiver to look for the REGISTRATION_COMPLETE broadcast
            startIDRegistrationService(); //start service to obtain the device id from GCM
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            Handler for the login_button. Extracts the inputted username and password and passes them to authenticate() */
    public void login( View view ) {
        authenticate(username.getText().toString(), password.getText().toString(), true);
    }

    /*
    Authenticates the inputted username and password with the app server and stores the auth_token returned
    by the server.
    Displays error message if authentication fails.
     */
    public void authenticate(final String username, final String password, final boolean update) {
        /*In the case that obtaining device id somehow failed, do not attempt to login*/
        if( deviceID.equals(Constants.NO_DEVICE)){
            Toast.makeText(this, "Error: Failed to obtain device id. Please check your connection", Toast.LENGTH_LONG).show();
            recreate(); //Restart the activity so we can try to get device id again.
        }
        else {
            Log.d(TAG, URLS.TOKEN_AUTH.string);
            String url = URLS.TOKEN_AUTH.string;
            StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String token = jsonResponse.getString(USERS.TOKEN.string);
                                System.out.println("Token: " + token);

                            /*Store the token in SharedPreferences*/
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(PreferencesKeys.AUTH_TOKEN, token);
                                editor.commit();

                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class); //Prepare intent to go to Home Screen

                                /*Compare device id obtained from GCM with the device id stored in preferences to see if user is on a different device and update*/
                                if (update) {
                                    updateDeviceID(homeIntent); //Update device ID on server, and start HomeActivity after its successful
                                }
                                /*Arrived here from RegistrationActivity, no need to update device id*/
                                else{
                                    startActivity(homeIntent);
                                    LoginActivity.this.finish();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new DetailedErrorListener(LoginActivity.this)
            ) {
                @Override
                //Create the body of the request
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // Get the registration info from input fields and add them to the body of the request
                    params.put(USERS.USERNAME.string, username);
                    params.put(USERS.PASSWORD.string, password);
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };

            //TODO: LATER WHEN USING HEADERS, MAKE A GENERIC STRINGREQUEST CLASS THAT ALREADY HAS THE getHeaders() method

            MyApplication.requestQueue.add(loginRequest);
        }
    }

    /*
        Updates the user's device id on the server, and starts the HomeActivity on success.
        @param homeIntent - Intent to go to HomeActivity once the device ID has been updated successfully.
     */
    private void updateDeviceID(final Intent homeIntent){
    /*Send a user-detail request to update userprofile with new device ID, and store in preferences*/
        String url = URLS.USER_DETAIL.string;
        StringRequest updateDeviceRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Updated device ID" );
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PreferencesKeys.DEVICE_ID, deviceID);
                        editor.commit();

                        startActivity(homeIntent);
                        LoginActivity.this.finish();
                    }
                },
                new DetailedErrorListener(LoginActivity.this)
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Log.d("DeviceIDBeforeRequest", deviceID);
                params.put(USERS.DEVICE_ID.string, deviceID);
                return params;
            }

            @Override
            //Add header of request
            public Map<String, String> getHeaders() {
                return MyApplication.getAuthenticationHeader(LoginActivity.this);
            }
        };
        MyApplication.requestQueue.add(updateDeviceRequest);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenBroadCastReceiver); //unregister this receiver if activity destroyed.
    }
}