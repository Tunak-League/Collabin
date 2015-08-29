package tunakleague.com.redemption.profiles;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tunakleague.com.redemption.app_constants.Constants;
import tunakleague.com.redemption.DetailedErrorListener;
import tunakleague.com.redemption.DrawerActivity;
import tunakleague.com.redemption.MyApplication;
import tunakleague.com.redemption.R;
import tunakleague.com.redemption.app_constants.ServerConstants.*;

public class ProfileActivity extends DrawerActivity implements BaseProjectListFragment.OnProjectSelectedListener
, ProjectListCreateFragment.OnProjectCreateListener, ProfileUpdateFragment.HideTabsListener{
    public static final String TAG = "ProfileActivity";
    public static final String SKILL_NAME_FIELD = "skill_name";

    private TabLayout tabLayout;

    private List<String> skillsCollection = new ArrayList<String>(); //List of all skills on the server's database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        mDrawerLayout.addView(contentView, 0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ProfileFragmentPagerAdapter(getSupportFragmentManager(),
                ProfileActivity.this));
        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        /*Displays the Projects tab upon opening if the last action completed was a project update or project create*/
        if( getIntent().getAction() != null && getIntent().getAction().equals(Constants.ACTION_PROJECT)) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        }

        /*Retrieve the server database's complete list of skills to be used for auto-complete during skill entry*/
        String url = URLS.SKILLS.string;
        JsonArrayRequest skillsRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for( int i = 0; i < response.length(); i++ ){
                            try {
                                skillsCollection.add(response.getJSONObject(i).getString(SKILL_NAME_FIELD));
                            } catch (JSONException e) {
                                Log.d(TAG, "Failed to retrieve skills collection");
                            }
                        }
                    }
                },
                new DetailedErrorListener(this)
        );
        MyApplication.requestQueue.add(skillsRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
    Callback interface method for BaseProjectListFragment
     */
    @Override
    public void onProjectSelected(JSONObject project) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       fragmentTransaction.add(R.id.profile, (Fragment) ProjectUpdateFragment.newInstance(project));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        setTabsVisible(false);
    }

    /*
        Callback interface method for BaseProjectListFragment
     */
    @Override
    public void onCreateProject() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.profile, (Fragment) ProjectCreateFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTabsVisible(false);
    }

    /*
    Callback interface method for ProfileUpdateFragment
    */
    @Override
    public void setTabsVisible(boolean visible) {
        tabLayout.setVisibility((visible == true) ? View.VISIBLE : View.INVISIBLE);

    }

    /*Return the collection of skills stored in the server's database*/
    public List<String> getSkillsCollection() {
        return skillsCollection;
    }
}
