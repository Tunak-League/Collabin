package tunakleague.com.redemption.profiles;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {
    /*
    Controls order of the tabs, tab titles, and fragments associated with each tab
     */

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Personal", "Projects"};
    private Context context;

    public ProfileFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public Fragment getItem(int position) {
        if( position == 0)
          return UserUpdateFragment.newInstance();
        else
            return ProjectListCreateFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
