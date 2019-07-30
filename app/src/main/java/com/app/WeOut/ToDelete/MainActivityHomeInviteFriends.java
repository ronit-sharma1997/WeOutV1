package com.app.WeOut.ToDelete;

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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Event;
import utils.Friend;
import utils.Friend_withCheck;
import utils.InviteFriendsRecyclerViewAdapter;
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

    public void onClick_Finish(View view) {
        // First check if any friends are selected
        HashMap <String, Boolean> friendsCheckedMap = new HashMap<>();
        Friend_withCheck friend;

        for (int i = 0; i < friendList.size(); i++) {
            friend = friendList.get(i);
            if (friend.isChecked()) friendsCheckedMap.put(friend.getUserName(), true);
        }

        Log.d(TAG, "Checked Friends: " + friendsCheckedMap.keySet().toString());

        // Create intent to take you from Inviting Friends to Home Page
        final Intent intent = new Intent();

        // Get event information from previous intent
        String newEventJson = getIntent().getStringExtra("newEventJson");
        // Convert this information into an event object
        Event event = new Gson().fromJson(newEventJson, Event.class);
        // Add friends checked map to the event object
        event.setInvitedMap(friendsCheckedMap);

        // Create the event in the database
        Utilities.createEventInDatabase(event, view, getApplicationContext());

//
//        // Create a new JSON to send to the next activity
//        String updatedEventJson = new Gson().toJson(event);
//        Log.d(TAG, updatedEventJson);
//        // Pass this event info along to the next activity
//        intent.putExtra("newEventJson", updatedEventJson);



//        // Feedback to show event has been created
//        Toast.makeText(getApplicationContext(), "Event Created Successfully.", Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                setResult(Activity.RESULT_OK, intent);
//                startActivity(intent);
                finish();
            }
        }, 2000);

//        // Switch activities

    }
}
