package utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.WeOut.MainActivityHomeFragment;
import com.app.WeOut.MainActivityMyProfileFragment;
import com.app.WeOut.MainActivityNotificationsFragment;

/**
 * Class that extends {@link FragmentStatePagerAdapter} to handle clicking of the tab layout in
 * {@link com.app.WeOut.MainActivity} and generate correct fragment based on tab selection.
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

    private MainActivityHomeFragment tab1;
    private MainActivityNotificationsFragment tab2;
    private MainActivityMyProfileFragment tab3;
    private int numberOfSubActivities;

    public MainActivityPagerAdapter(FragmentManager fm, int numberOfSubActivities) {
        super(fm);
        this.numberOfSubActivities = numberOfSubActivities;
        this.tab1 = new MainActivityHomeFragment();
        this.tab2 = new MainActivityNotificationsFragment();
        this.tab3 = new MainActivityMyProfileFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            case 2:
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.numberOfSubActivities;
    }
}
