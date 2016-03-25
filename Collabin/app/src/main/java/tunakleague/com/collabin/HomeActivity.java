package tunakleague.com.collabin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import tunakleague.com.collabin.search.ProjectSelectActivity;
import tunakleague.com.collabin.search.UserSearchActivity;

public class HomeActivity extends DrawerActivity {
    public static final int TAB_LIMIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Collabin");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        mDrawerLayout.addView(contentView, 0);

        TextView homeText = new TextView(getApplicationContext());
        homeText.setText("Home");
        homeText.setTextColor(Color.parseColor("#FFFFFF"));

        TextView matchesText = new TextView(getApplicationContext());
        matchesText.setText("My Matches");
        matchesText.setTextColor(Color.parseColor("#FFFFFF"));

        TextView projectMatchesText = new TextView(getApplicationContext());
        projectMatchesText.setText("My Projects Matches");
        projectMatchesText.setTextColor(Color.parseColor("#FFFFFF"));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setCustomView(homeText));
        tabLayout.addTab(tabLayout.newTab().setCustomView(matchesText));
        tabLayout.addTab(tabLayout.newTab().setCustomView(projectMatchesText));
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
        viewPager.setOffscreenPageLimit(TAB_LIMIT);
    }

    /*Handler for clicking "Find Projects" button. Launches UserSearchActivity*/
    public void findProjects( View v ){
        Intent userSearchIntent = new Intent(this, UserSearchActivity.class );
        startActivity(userSearchIntent);
    }

    /*Handler for clicking "Find Collaborators" button. Launches a ProjectSelectActivity */
    public void findCollaborators( View v) {
        Intent projectSelectIntent = new Intent(this, ProjectSelectActivity.class);
        startActivity(projectSelectIntent);
    }


}
