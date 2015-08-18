package tunakleague.com.redemption;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeFragment home = new HomeFragment();
                return home;
            case 1:
                MatchesFragment matches = new MatchesFragment();
                return matches;
            case 2:
                ProjectsMatchesFragment projectMatches = new ProjectsMatchesFragment();
                return projectMatches;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}