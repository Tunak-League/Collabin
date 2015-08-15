package tunakleague.com.redemption.profiles;

import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONObject;

import tunakleague.com.redemption.DrawerActivity;
import tunakleague.com.redemption.R;

public class ProfileActivity extends DrawerActivity implements ProjectListFragment.OnProjectSelectedListener
, ProjectFragment.OnProjectUpdatedListener{

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
        fragmentTransaction.add(R.id.profile, (Fragment) ProjectFragment.newInstance(project, position));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        tabLayout.setVisibility(View.INVISIBLE);
    }

    /*
    Callback interface method for ProjectFragment
     */
    @Override
    public void onProjectUpdated(JSONObject project, int position) {
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        FragmentPagerAdapter a = (FragmentPagerAdapter) pager.getAdapter();
        ProjectListFragment fragment = (ProjectListFragment) a.instantiateItem(pager, 1);

        if( fragment != null )
            fragment.updateProject(project, position);
        else
            Log.d(TAG, "Can't get projectlist");
    }

    @Override
    public void setTabVisible(boolean visible ){
        tabLayout.setVisibility( (visible == true ) ? View.VISIBLE : View.INVISIBLE );
    }
}
