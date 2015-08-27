package tunakleague.com.redemption;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tunakleague.com.redemption.app_constants.PreferencesKeys;
import tunakleague.com.redemption.authentication.WelcomeActivity;
import tunakleague.com.redemption.profiles.ProfileActivity;


public abstract class DrawerActivity extends AppCompatActivity {
    /*
    Extended by any class that wants to include the main navigation drawer and action bar
     */
    public static final String TAG = "DrawerActivity";

    private String[] navigation_options;
    protected DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    /*Constants for items in the navigation drawer*/
    public static final int LOGOUT = 0;
    public static final int PROFILE = 1;
    public static final int HOME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        navigation_options = getResources().getStringArray(R.array.navigation_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, navigation_options));

        /*Specify which actions to take when an item is selected from the navigation drawer*/
        mDrawerList.setOnItemClickListener( new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case LOGOUT:
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove(PreferencesKeys.AUTH_TOKEN); //Clear the token
                        editor.commit();

//                       /*Return to the welcome page*/
                        Intent welcomeIntent = new Intent(DrawerActivity.this, WelcomeActivity.class);
                        startActivity(welcomeIntent);
                        break;
                    case PROFILE:
                        Intent profileIntent = new Intent(DrawerActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                    case HOME:
                        Intent homeIntent = new Intent(DrawerActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        break;
                    default:
                        Log.d(TAG, "WHAT DID YOU CLICK?????");
                }
            }
        });



         mDrawerToggle = new ActionBarDrawerToggle
                 (this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
         };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

}
