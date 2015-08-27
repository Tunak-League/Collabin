package tunakleague.com.redemption;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import tunakleague.com.redemption.search.ProjectSelectActivity;
import tunakleague.com.redemption.search.UserSearchActivity;

public class HomeActivity extends DrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        mDrawerLayout.addView(contentView, 0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("My Matches"));
        tabLayout.addTab(tabLayout.newTab().setText("My Projects Matches"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /*Handler for clicking "Find Projects" button. Launches UserSearchActivity*/
    public void findProjects( View v ){
        Log.d(TAG, "Clicked Find Projects");
        Intent userSearchIntent = new Intent(this, UserSearchActivity.class );
        startActivity(userSearchIntent);
    }

    /*Handler for clicking "Find Collaborators" button. Launches a ProjectSelectActivity */
    public void findCollaborators( View v) {
        Log.d( TAG, "clicked Find Collab" );
        Intent projectSelectIntent = new Intent( this, ProjectSelectActivity.class);
        startActivity( projectSelectIntent);
    }


}
