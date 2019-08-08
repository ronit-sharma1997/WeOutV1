package com.app.WeOut;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import com.app.WeOut.dummy.DummyContent;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import utils.Event;
import utils.Event_withID;
import utils.MainActivityPagerAdapter;

/**
 * MainActivity extends {@link AppCompatActivity} and focuses on controlling the flow of the overall
 * flow of the application using a TabLayout and Fragments
 */
public class MainActivity extends AppCompatActivity
    implements MainActivityHomeFragment.OnFragmentInteractionListener,
    MainActivityMyProfileFragment.OnFragmentInteractionListener,
    MainActivityNotificationsFragment.OnFragmentInteractionListener,
    Profile_FriendListFragment.OnListFragmentInteractionListener,
    Profile_AddFriendFragment.OnFragmentInteractionListener,
    Profile_FriendRequestsFragment.OnListFragmentInteractionListener,
    MainActivityNotificationsFragment.OnListFragmentInteractionListener,
    MainActivityAddEventInviteFriendsFragment.OnListFragmentInteractionListener,
    MainActivityHomeFragment.OnListFragmentInteractionListener {

  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Create TabLayout and associate with XML item
    TabLayout tabLayout = findViewById(R.id.mainToolbar);

    // Add tabs for each screen (home, notifications, friends)
    tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
    tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.notifications));
    tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friends));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    // View Pager to hold multiple fragments
    final ViewPager viewPager = findViewById(R.id.pager);
    final MainActivityPagerAdapter myAdapter = new MainActivityPagerAdapter(
        getSupportFragmentManager(), tabLayout.getTabCount());
    viewPager.setAdapter(myAdapter);

    // Color is set for the icon when it is selected
    int color = Color.parseColor("#000000");
    tabLayout.getTabAt(viewPager.getCurrentItem()).getIcon()
        .setColorFilter(color, PorterDuff.Mode.MULTIPLY);

    // When a new tab is selected,
    // set that current item to the view pager and set the color
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

  @Override
  public void onListFragmentInteraction(Event_withID event, View v, String adapterType) {
    Log.d(TAG, event.getEvent().getTitle() + " was clicked from " + adapterType);

    //Create an intent and prepare to send event information to EventHomeFeedDetailsActivity
    //to display the information
    Intent intent = new Intent(this, EventHomeFeedDetailsActivity.class);
    intent.putExtra("deleteEvents", adapterType.equals("eventInvites") ? false : true);

    // Convert the event information into a JSON
    Gson gson = new Gson();
    String eventJson = gson.toJson(event);

    intent.putExtra("eventDetails", eventJson);

    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
  }

}
