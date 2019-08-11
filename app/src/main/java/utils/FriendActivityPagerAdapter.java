package utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.WeOut.Profile_AddFriendFragment;
import com.app.WeOut.Profile_FriendListFragment;
import com.app.WeOut.Profile_FriendRequestsFragment;

/**
 * Class that extends {@link FragmentStatePagerAdapter} to handle clicking of the tab layout in
 * {@link com.app.WeOut.MainActivityMyProfileFragment} and generates the right fragment based on tab selection.
 */
public class FriendActivityPagerAdapter extends FragmentStatePagerAdapter {

    private Profile_FriendListFragment tab1;
    private Profile_AddFriendFragment tab2;
    private Profile_FriendRequestsFragment tab3;
    private int numberOfSubActivities;
    private String currUserFullName;

    public FriendActivityPagerAdapter(FragmentManager fm, int numberOfSubActivities, String currUserFullName) {
        super(fm);
        this.numberOfSubActivities = numberOfSubActivities;
        this.currUserFullName = currUserFullName;
        this.tab1 = new Profile_FriendListFragment();
        this.tab2 = new Profile_AddFriendFragment(this.currUserFullName);
        this.tab3 = new Profile_FriendRequestsFragment(this.currUserFullName);
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

