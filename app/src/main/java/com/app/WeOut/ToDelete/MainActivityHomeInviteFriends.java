package com.app.WeOut.ToDelete;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.WeOut.R;

import java.util.ArrayList;

public class MainActivityHomeInviteFriends extends AppCompatActivity {

    RecyclerView listView_DisplayFriends;
    Button btn_Finish;

    ArrayList<String> friendList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_invite_friends);

        btn_Finish = findViewById(R.id.inviteFriends_FinishButton);
        listView_DisplayFriends = findViewById(R.id.friendInviteList);

        friendList.add("Saif");
        friendList.add("Ronit");
        friendList.add("Pratheep");

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                friendList);
//
//        listView_DisplayFriends.setAdapter(arrayAdapter);
    }

    public void onClick_Finish(View view) {
        // Create intent to take you from Inviting Friends to Home Page
        Intent intent = new Intent();

        String newEventJson = getIntent().getStringExtra("newEventJson");
        intent.putExtra("newEventJson", newEventJson);

        // Feedback to show event has been created
        Toast.makeText(getApplicationContext(), "Event Created Successfully.", Toast.LENGTH_SHORT).show();

        // Switch activities
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
