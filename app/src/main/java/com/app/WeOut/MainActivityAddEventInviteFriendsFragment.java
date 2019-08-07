package com.app.WeOut;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.View.OnClickListener;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Event;
import utils.Friend_withCheck;
import utils.InviteFriendsRecyclerViewAdapter;
import utils.User;
import utils.Utilities;


/**
 * MainActivityAddEventInviteFriendsFragment extends {@link Fragment} subclass and focuses on adding
 * friends to invite to an event created by the user.
 */
public class MainActivityAddEventInviteFriendsFragment extends Fragment {

    private ArrayList<Friend_withCheck> friendList;

    //Buttons for Screen
    private Button btn_Finish;
    private ImageButton closeScreen, backButton;
    private FloatingActionButton addEventFAB;

    //listener for List
    private OnListFragmentInteractionListener mListener;

    //Components of Recycler View
    private RecyclerView recyclerView_InviteFriendsList;
    private InviteFriendsRecyclerViewAdapter myFriendRecyclerViewAdapter;

    //Components of Screen
    private CardView inviteFriendsList;
    private FrameLayout createEventContainer;

    private final static int ANIMATION_DURATION = 150;


    private float fabOriginX;
    private float fabOriginY;

    // Private variables
    private String TAG = "EventCreation: ";


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

        // Associate XML components by ID
        this.recyclerView_InviteFriendsList = view.findViewById(R.id.friendInviteList);
        this.inviteFriendsList = view.findViewById(R.id.inviteFriendsCardView);
        this.btn_Finish =view.findViewById(R.id.inviteFriends_FinishButton);
        this.btn_Finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_Finish(view);
            }
        });
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

        this.friendList = new ArrayList<>();

        this.recyclerView_InviteFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.myFriendRecyclerViewAdapter = new InviteFriendsRecyclerViewAdapter(friendList);
        this.recyclerView_InviteFriendsList.setAdapter(myFriendRecyclerViewAdapter);

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
        addEventFAB.setImageResource(R.drawable.plus);
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


    private void onClick_Finish(final View view) {

        // Create a Hash Map for invited friends for the event
        final HashMap<String, String> friendsCheckedMap = new HashMap<>();
        Friend_withCheck friend;

        // Check if any friends are selected. If they are, add them to the map.
        for (int i = 0; i < friendList.size(); i++) {
            friend = friendList.get(i);
            if (friend.isChecked()) {
                friendsCheckedMap.put(friend.getUserName(), friend.getFullName());
            }
        }
        Log.d(TAG, "Checked Friends: " + friendsCheckedMap.keySet().toString());

        // Create a hash map for accepted friends for the event
        final HashMap<String, String> acceptedFriendsMap = new HashMap<>();

        // Get current username
        final String currUsername = Utilities.getCurrentUsername();

        // Get current user full name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df_currUser = db.collection("users").document(currUsername);

        df_currUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists() && documentSnapshot != null) {

                    String currUserFullName = documentSnapshot.toObject(User.class).getFullName();

                    // Add one entry to the accepted map (the organizer AKA curr user)
                    acceptedFriendsMap.put(currUsername, currUserFullName);

                    Log.d(TAG, "Getting full name from current user was successful. [" +
                        currUsername + ", " + currUserFullName + "]");

                    // Get event information from previous intent
                    String newEventJson = getArguments().getString("newEventJson");
                    // Convert this information into an event object
                    Event event = new Gson().fromJson(newEventJson, Event.class);

                    // Add friends checked map to the event object
                    event.setInvitedMap(friendsCheckedMap);
                    // Add friends accepted map to the event object
                    event.setAttendingMap(acceptedFriendsMap);

                    // Create the event in the database
                    Utilities.createEventInDatabase(event, view, getActivity());
                    closeScreen();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,
                    "Failure to get " + currUsername + " 's full name. Error: " + e.getMessage());
                Utilities.displaySnackBar(view, getActivity(),
                    "Error retrieving user information.");
                acceptedFriendsMap.put(currUsername, "Error Name");
            }
        });
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
