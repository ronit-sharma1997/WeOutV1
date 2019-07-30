package com.app.WeOut;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.app.WeOut.dummy.DummyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import utils.InviteFriendRecyclerViewAdapter;


/**
 * MainActivityAddEventInviteFriendsFragment extends {@link Fragment} subclass and focuses on adding
 * friends to invite to an event created by the user.
 */
public class MainActivityAddEventInviteFriendsFragment extends Fragment {

    private RecyclerView listView_DisplayFriends;
    private Button btn_Finish;
    private List<String> friends;
    private InviteFriendRecyclerViewAdapter myFriendRecyclerViewAdapter;
    private OnListFragmentInteractionListener mListener;
    private ImageButton closeScreen, backButton;
    private CardView inviteFriendsList;
    private final static int ANIMATION_DURATION = 150;
    private FrameLayout createEventContainer;
    private FloatingActionButton addEventFAB;
    private float fabOriginX;
    private float fabOriginY;


    public MainActivityAddEventInviteFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater
                .inflate(R.layout.fragment_main_activity_add_event_invite_friends, container, false);
        this.btn_Finish = view.findViewById(R.id.inviteFriends_FinishButton);
        this.friends = new ArrayList<>();
        this.friends.add("Saif");
        this.friends.add("Ronit");
        this.listView_DisplayFriends = view.findViewById(R.id.friendInviteList);
        this.inviteFriendsList = view.findViewById(R.id.inviteFriendsCardView);
        this.closeScreen = view.findViewById(R.id.closeScreenInviteFriends);
        this.backButton = view.findViewById(R.id.backButtonInviteFriends);
        this.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviousScreen();
            }
        });
        this.createEventContainer = getActivity().findViewById(R.id.createEventScreen);
        this.closeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeScreen();
            }
        });
        this.listView_DisplayFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.myFriendRecyclerViewAdapter = new InviteFriendRecyclerViewAdapter(friends, mListener);
        this.listView_DisplayFriends.setAdapter(myFriendRecyclerViewAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Method to set a reference to the add event floating action button
     *
     * @param fab - reference to FAB
     * @param x - original x coordinate of FAB on phone screen
     * @param y - original y coordinate of FAB on phone screen
     */
    public void setAddEventFAB(FloatingActionButton fab, float x, float y) {
        this.addEventFAB = fab;
        this.fabOriginX = x;
        this.fabOriginY = y;
    }

    /**
     * Helper function to complete the process of Closing the current screen.
     */
    private void closeScreen() {
        //animate the RecyclerView so it disappears in duration ANIMATION_DURATION
        ViewPropertyAnimator animator = this.inviteFriendsList.animate()
                .scaleX(0).scaleY(0)
                .setDuration(ANIMATION_DURATION);
        animator.start();
        //animation the createEventContainer so that it disppears in duration ANIMATION_DURATION
        createEventContainer.animate().scaleX(0).scaleY(0).setDuration(ANIMATION_DURATION)
                .setStartDelay(50).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                createEventContainer.setVisibility(View.GONE);
            }
        }).start();

        //we restablish the floating action button by setting its original x and y coordinates
        addEventFAB.setX(this.fabOriginX);
        addEventFAB.setY(this.fabOriginY);
        addEventFAB.setImageResource(R.drawable.ic_add_black_24dp);
        addEventFAB.show();

        //we restablish the tab layout by making it visible again
        TabLayout tabBar = getActivity().findViewById(R.id.mainToolbar);
        tabBar.setVisibility(View.VISIBLE);
    }

    /**
     * Helper method to complete the process of going back to the previous screen with event details
     */
    private void goToPreviousScreen() {
        MainActivityAddEventFragment fragment = new MainActivityAddEventFragment();
        fragment.setAddEventFAB(this.addEventFAB, this.fabOriginX, this.fabOriginY);
        //begin transaction and create custom animation
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                .replace(R.id.createEventScreen, fragment).commit();
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other fragments
     * contained in that activity.
     * <p/>
     * See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

}
