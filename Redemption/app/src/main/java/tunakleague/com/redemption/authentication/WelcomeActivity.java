package tunakleague.com.redemption.authentication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import tunakleague.com.redemption.R;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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
        Handler for signup_button click.
        Starts RegistrationActivity
     */
    public void launchSignup(View view ) {
        //Launch the signup page
        Intent signupIntent = new Intent(this, RegistrationActivity.class);
        startActivity(signupIntent);
        Log.d(TAG, "WTF IS THIS");
    }

    /*
    Handler for signin button click
    Starts LoginActivity
     */
    public void launchSignin(View view ) {
        Intent signinIntent = new Intent( this, LoginActivity.class);
        startActivity(signinIntent);
    }

    /*
    Make back button press do nothing
     */
    @Override
    public void onBackPressed() {

    }
}
