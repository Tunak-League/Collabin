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
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tunakleague.com.redemption.HomeActivity;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.PreferencesKeys;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.ServerConstants;
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
    Handler for the login_button
    Authenticates the inputted username and password with the app server and stores the auth_token returned
    by the server.
    Displays error message if authentication fails.
     */
    //TODO: Move the whole method body into a method independent of button press. Conditionally call it (via intent data pass Username/pass) after Registration
    //And then for the button make some onLoginClick() method which calls login()
    public void login(View view ) {
        Log.d(TAG, URLS.TOKEN_AUTH.string);
        String url = URLS.TOKEN_AUTH.string;
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String token = jsonResponse.getString(USERS_TABLE.TOKEN.string );
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
                new Response.ErrorListener(){ //TODO: IMPLEMENT A GENERIC REST_SERVER_ERROR CLASS SO WE DON'T WRITE THIS OUT EVERYTIME.
                    public void onErrorResponse(VolleyError error) {
                        String json = null;

                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            switch(response.statusCode){
                                case 400:
                                    json = new String(response.data);
                                    json = trimMessage(json, "non_field_errors"); //TODO: CHANGE THIS FROM HARDCODED 'NON_FIELD_ERRORS' TO USING ALL VALUES???
                                    if(json != null) displayMessage(json);
                                    break;
                            }
                            //TODO: Additional cases. Idk maybe  a generic "SERVER ERROR" in case it's not 400
                        }
                    }

                    public String trimMessage(String json, String key){ //TODO: GET RID OF THE SQUARE BRACKETS SOMEHOW
                        String trimmedString = null;

                        try{
                            JSONObject obj = new JSONObject(json);
                            trimmedString = obj.getString(key);
                        } catch(JSONException e){
                            e.printStackTrace();
                            return null;
                        }

                        return trimmedString;
                    }

                    //Somewhere that has access to a context
                    public void displayMessage(String toastString){
                        Toast.makeText(LoginActivity.this, toastString, Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();

                // Get the registration info from input fields and add them to the body of the request
                params.put(USERS_TABLE.USERNAME.string, username.getText().toString() );
                params.put(USERS_TABLE.PASSWORD.string, password.getText().toString());
                params.put("Content-Type","application/json");
                return params;
            }
        };

        //TODO: LATER WHEN USING HEADERS, MAKE A GENERIC STRINGREQUEST CLASS THAT ALREADY HAS THE getHeaders() method

        MyApplication.requestQueue.add(loginRequest);

    }
}
