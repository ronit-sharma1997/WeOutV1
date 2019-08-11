package com.app.WeOut;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import utils.Event;
import utils.EventDetailsInvitee;
import utils.EventHomeFeedClickedInviteesRecyclerViewAdapter;
import utils.Event_withID;
import utils.Utilities;

/**
 * EventHomeFeedDetails Activity is a class that extends{@link AppCompatActivity} and is focused on
 * displaying detail such as title, location, date, and attendees for an Event.
 */
public class EventHomeFeedDetailsActivity extends AppCompatActivity {
    private TextView eventTitle, eventLocation, eventDate, totalInvitees, totalAcceptedInvitees,
        eventDescription, eventOrganizer;
    private ImageButton closeScreen, deleteEvent;
    private RecyclerView eventGuests;
    private EventHomeFeedClickedInviteesRecyclerViewAdapter myAdapter;
    private final String TAG = "EventHomeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_home_feed_details);

        //Assign Views Based on Ids
        this.eventTitle = findViewById(R.id.eventHomeFeedClickedTitle);
        this.eventLocation = findViewById(R.id.eventHomeFeedClickedLocation);
        this.eventDate = findViewById(R.id.eventHomeFeedClickedDate);
        this.eventGuests = findViewById(R.id.recyclerViewEventHomeFeedClickedInvitees);
        this.eventGuests.setNestedScrollingEnabled(false);
        this.totalInvitees = findViewById(R.id.eventHomeFeedClickedTotalIntendees);
        this.totalAcceptedInvitees = findViewById(R.id.eventHomeFeedClickedAcceptedIntendees);
        this.eventDescription = findViewById(R.id.eventHomeFeedClickedDescription);
        this.eventOrganizer = findViewById(R.id.eventHomeFeedClickedOrganizer);

        //Buttons on screen for closing and deleting an event
        this.closeScreen = findViewById(R.id.closeEventDetailButton);
        this.deleteEvent = findViewById(R.id.deleteEventButton);

        // Convert this information into an event object
        final Event_withID eventWithIDDetailFromFeed = new Gson().fromJson(getIntent().getStringExtra("eventDetails"), Event_withID.class);

        final Event eventDetailFromFeed = eventWithIDDetailFromFeed.getEvent();

        final List<EventDetailsInvitee> inviteesForEvent = new ArrayList<>();

        //we create a list of EventDetailsInvitee objects containing userName, first, last name and that they are attending the event
        for(Entry<String, String> invitee : eventDetailFromFeed.getAttendingMap().entrySet()) {
            String userName = invitee.getKey();
            String fullName = invitee.getValue();
            inviteesForEvent.add(new EventDetailsInvitee(userName, fullName.split(" ")[0], fullName.split(" ")[1], true));
        }

        int attendingCount = eventDetailFromFeed.getAttendingMap().size();

        this.totalAcceptedInvitees.setText(String.valueOf(attendingCount) + " yes");

        //we create a list of EventDetailsInvitee objects containing userName, first, last name and that they are not attending the event
        for(Entry<String, String> invitee : eventDetailFromFeed.getInvitedMap().entrySet()) {
            String userName = invitee.getKey();
            String fullName = invitee.getValue();
            inviteesForEvent.add(new EventDetailsInvitee(userName, fullName.split(" ")[0], fullName.split(" ")[1], false));
        }

        //calculate total invitees
        this.totalInvitees.setText(String.valueOf(eventDetailFromFeed.getInvitedMap().size() + attendingCount) + " guests");

        //set text for information relating to an Event such as title, location, date
        this.eventTitle.setText(eventDetailFromFeed.getTitle());

        this.eventLocation.setText(eventDetailFromFeed.getLocation());

        this.eventDate.setText(eventDetailFromFeed.getEventDate() + " " + eventDetailFromFeed.getEventTime());

        this.eventOrganizer.setText(eventDetailFromFeed.getOrganizer());

        this.eventDescription.setText(eventDetailFromFeed.getDescription());

        //create a Custom Adapter for Our RecyclerView listing invitees
        this.myAdapter = new EventHomeFeedClickedInviteesRecyclerViewAdapter(inviteesForEvent);

        this.eventGuests.setAdapter(this.myAdapter);

        this.closeScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });

        //check if the event details should have the delete button. If not hide it
        if (!getIntent().getBooleanExtra("deleteEvents", false)) {
          this.deleteEvent.setVisibility(View.GONE);
        } else {
          this.deleteEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
              //get Current Username of device
              String userName = Utilities.getCurrentUsername();

              //if we are the organizer for the event and delete, need to remove from the rest of
              //the invitees
              if (userName.equals(eventDetailFromFeed.getOrganizer())) {
                try {
                  Utilities.deleteEventAsOrganizer(eventWithIDDetailFromFeed, view,
                      getApplicationContext());
                  // Wait a bit to finish this activity
                  final Handler handler = new Handler();
                  handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      // Do something after 5s = 5000ms
                      finish();
                    }
                  }, 1000);
                } catch (IllegalStateException e) {

                }


              }
              //we need to delete the event from the user's events collection under the accepted document and we need to delete the event
              //from the event's attendingMap
              else {
                try {
                  Utilities.deleteEventNonOrganizer(eventWithIDDetailFromFeed, view,
                      getApplicationContext());
                  // Wait a bit to finish this activity
                  final Handler handler = new Handler();
                  handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      // Do something after 5s = 5000ms
                      finish();
                    }
                  }, 1000);
                } catch (IllegalStateException e) {

                }

              }
            }
          });
        }
    }
}

