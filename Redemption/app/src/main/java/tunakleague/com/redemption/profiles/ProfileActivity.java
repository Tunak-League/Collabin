package tunakleague.com.redemption.profiles;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import tunakleague.com.redemption.Constants;
import tunakleague.com.redemption.DrawerActivity;
import tunakleague.com.redemption.R;

public class ProfileActivity extends DrawerActivity implements ProjectListFragment.OnProjectActionListener
, ProfileFragment.HideTabsListener{

    private TabLayout tabLayout;
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

        if( getIntent().getAction() != null && getIntent().getAction().equals(Constants.ACTION_PROJECT)) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        }
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
    Callback interface method for ProjectListFragment
     */
    @Override
    public void onProjectSelected(JSONObject project, int position) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.profile, (Fragment) ProjectUpdateFragment.newInstance(project, position));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTabsVisible(false);
    }

    @Override
    public void onCreateProject() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.profile, (Fragment) ProjectCreateFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        setTabsVisible(false);
    }

    @Override
    public void setTabsVisible(boolean visible) {
        tabLayout.setVisibility( (visible == true ) ? View.VISIBLE : View.INVISIBLE );

    }
}
