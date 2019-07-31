package com.app.WeOut.ToDelete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.WeOut.MainActivity;
import com.app.WeOut.MainActivityHomeFragment;
import com.app.WeOut.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Event;
import utils.Friend;
import utils.Friend_withCheck;
import utils.InviteFriendsRecyclerViewAdapter;
import utils.User;
import utils.Utilities;

public class MainActivityHomeInviteFriends extends AppCompatActivity {

    private RecyclerView recyclerView_InviteFriendsList;
    private ArrayList<Friend_withCheck> friendList;

    // XML Components
    Button btn_Finish;

    // Private variables
    private String TAG = "EventCreation: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_invite_friends);

        // Associate XML components by ID
        btn_Finish = findViewById(R.id.inviteFriends_FinishButton);
        recyclerView_InviteFriendsList = findViewById(R.id.friendInviteList);

        // Initialize Friends List
        friendList = new ArrayList<>();

        // Create and set a new adapter
        InviteFriendsRecyclerViewAdapter adapter = new InviteFriendsRecyclerViewAdapter(friendList);
        recyclerView_InviteFriendsList.setAdapter(adapter);
    }

    public void onClick_Finish(final View view) {

        // Create a Hash Map for invited friends for the event
        final HashMap <String, String> friendsCheckedMap = new HashMap<>();
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
        final HashMap <String, String> acceptedFriendsMap = new HashMap<>();

        // Get current username
        final String currUsername = Utilities.getCurrentUsername();

        // Get current user full name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference df_currUser = db.collection("users").document(currUsername);

        df_currUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists() && documentSnapshot != null) {

                    String currUserFullName = documentSnapshot.toObject(User.class).getFullName();

                    // Add one entry to the accepted map (the organizer AKA curr user)
                    acceptedFriendsMap.put(currUsername, currUserFullName);

                    Log.d(TAG, "Getting full name from current user was successful. [" +
                            currUsername + ", " + currUserFullName + "]");

                    // Get event information from previous intent
                    String newEventJson = getIntent().getStringExtra("newEventJson");
                    // Convert this information into an event object
                    Event event = new Gson().fromJson(newEventJson, Event.class);

                    // Add friends checked map to the event object
                    event.setInvitedMap(friendsCheckedMap);
                    // Add friends accepted map to the event object
                    event.setAttendingMap(acceptedFriendsMap);

                    // Create the event in the database
                    Utilities.createEventInDatabase(event, view, getApplicationContext());

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure to get " + currUsername + " 's full name. Error: " + e.getMessage());
                Utilities.displaySnackBar(view, getApplicationContext(), "Error retrieving user information.");
                acceptedFriendsMap.put(currUsername, "Error Name");
            }
        });

        // Create intent to take you from Inviting Friends to Home Page
        final Intent intent = new Intent();

        // Wait a bit to finish this activity
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }, 1500);

    }
}
