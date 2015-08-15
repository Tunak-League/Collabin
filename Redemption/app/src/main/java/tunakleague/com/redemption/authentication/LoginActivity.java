package tunakleague.com.redemption.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.Constants;
import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.HomeActivity;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.PreferencesKeys;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants.*;

public class LoginActivity extends AppCompatActivity {
    public final String TAG = "LoginActivity";

    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username =  (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);

        Intent intent = getIntent();
        /*If LoginActivity was started from RegistrationActivity, retrieve passed username/password to login automatically*/
        if( intent.getAction() != null && intent.getAction().equals(Constants.ACTION_LOGIN)) {
            String username_input = intent.getExtras().getString( USERS.USERNAME.string );
            String password_input = intent.getExtras().getString( USERS.PASSWORD.string );
            Log.d(TAG, username_input);
            Log.d( TAG, password_input);
            authenticate( username_input, password_input );
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
        authenticate(username.getText().toString(), password.getText().toString());
    }

    /*

    Authenticates the inputted username and password with the app server and stores the auth_token returned
    by the server.
    Displays error message if authentication fails.
     */
    public void authenticate(final String username, final String password ) {
        Log.d(TAG, URLS.TOKEN_AUTH.string);
        String url = URLS.TOKEN_AUTH.string;
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String token = jsonResponse.getString(USERS.TOKEN.string );
                            System.out.println("Token: " + token);

                            /*Store the token in SharedPreferences*/
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( LoginActivity.this);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PreferencesKeys.AUTH_TOKEN, token );
                            editor.commit();

                            /*Go to HomeActivity*/
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                            LoginActivity.this.finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new DetailedErrorListener(LoginActivity.this)
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();

                // Get the registration info from input fields and add them to the body of the request
                params.put(USERS.USERNAME.string, username );
                params.put(USERS.PASSWORD.string, password);
                params.put("Content-Type","application/json");
                return params;
            }
        };

        //TODO: LATER WHEN USING HEADERS, MAKE A GENERIC STRINGREQUEST CLASS THAT ALREADY HAS THE getHeaders() method

        MyApplication.requestQueue.add(loginRequest);

    }
}
