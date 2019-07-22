package com.app.WeOut;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.app.WeOut.dummy.DummyContent;

import utils.MainActivityPagerAdapter;

public class MainActivity extends AppCompatActivity
        implements MainActivityHomeFragment.OnFragmentInteractionListener,
        MainActivityMyProfileFragment.OnFragmentInteractionListener,
        MainActivityNotificationsFragment.OnFragmentInteractionListener,
        FriendFragment.OnListFragmentInteractionListener,
        AddFriendFragment.OnFragmentInteractionListener,
        MyFriendRequestsFragment.OnListFragmentInteractionListener,
        MainActivityNotificationsFragment.OnListFragmentInteractionListener
        {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.mainToolbar);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.notifications));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friends));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final MainActivityPagerAdapter myAdapter = new MainActivityPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(myAdapter);
        int color = Color.parseColor("#000000");
        tabLayout.getTabAt(viewPager.getCurrentItem()).getIcon().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                int color = Color.parseColor("#000000");
                tab.getIcon().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int color = Color.parseColor("#FFFFFF");
                tab.getIcon().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
