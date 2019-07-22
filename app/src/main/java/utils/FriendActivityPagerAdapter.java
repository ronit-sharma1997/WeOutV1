package utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.WeOut.AddFriendFragment;
import com.app.WeOut.FriendFragment;
import com.app.WeOut.MyFriendRequestsFragment;

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
                FriendFragment tab1 = new FriendFragment();
                return tab1;
            case 1:
                AddFriendFragment tab2 = new AddFriendFragment();
                return tab2;
            case 2:
                MyFriendRequestsFragment tab3 = new MyFriendRequestsFragment();
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

