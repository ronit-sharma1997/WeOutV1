package utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.WeOut.Profile_AddFriendFragment;
import com.app.WeOut.Profile_FriendListFragment;
import com.app.WeOut.Profile_FriendRequestsFragment;

public class FriendActivityPagerAdapter extends FragmentStatePagerAdapter {

    int numberOfSubActivities;

    public FriendActivityPagerAdapter(FragmentManager fm, int numberOfSubActivities) {
        super(fm);
        this.numberOfSubActivities = numberOfSubActivities;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Profile_FriendListFragment tab1 = new Profile_FriendListFragment();
                return tab1;
            case 1:
                Profile_AddFriendFragment tab2 = new Profile_AddFriendFragment();
                return tab2;
            case 2:
                Profile_FriendRequestsFragment tab3 = new Profile_FriendRequestsFragment();
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

