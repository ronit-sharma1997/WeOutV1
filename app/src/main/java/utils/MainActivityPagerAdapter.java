package utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.WeOut.MainActivityHomeFragment;
import com.app.WeOut.MainActivityMyProfileFragment;
import com.app.WeOut.MainActivityNotificationsFragment;

public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

    int numberOfSubActivities;

    public MainActivityPagerAdapter(FragmentManager fm, int numberOfSubActivities) {
        super(fm);
        this.numberOfSubActivities = numberOfSubActivities;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                MainActivityHomeFragment tab1 = new MainActivityHomeFragment();
                return tab1;
            case 1:
                MainActivityNotificationsFragment tab2 = new MainActivityNotificationsFragment();
                return tab2;
            case 2:
                MainActivityMyProfileFragment tab3 = new MainActivityMyProfileFragment();
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
