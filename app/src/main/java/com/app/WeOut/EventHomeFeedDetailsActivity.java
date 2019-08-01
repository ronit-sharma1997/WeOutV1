package com.app.WeOut;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import utils.CustomSnackBar;
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
    private FirebaseFirestore dbReference;
    private final String TAG = "EventHomeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_home_feed_details);
        this.dbReference = FirebaseFirestore.getInstance();
        this.eventTitle = findViewById(R.id.eventHomeFeedClickedTitle);
        this.eventLocation = findViewById(R.id.eventHomeFeedClickedLocation);
        this.eventDate = findViewById(R.id.eventHomeFeedClickedDate);
        this.eventGuests = findViewById(R.id.recyclerViewEventHomeFeedClickedInvitees);
        this.totalInvitees = findViewById(R.id.eventHomeFeedClickedTotalIntendees);
        this.totalAcceptedInvitees = findViewById(R.id.eventHomeFeedClickedAcceptedIntendees);
        this.eventDescription = findViewById(R.id.eventHomeFeedClickedDescription);
        this.eventOrganizer = findViewById(R.id.eventHomeFeedClickedOrganizer);
        this.closeScreen = findViewById(R.id.closeEventDetailButton);
        this.deleteEvent = findViewById(R.id.deleteEventButton);

        // Convert this information into an event object
        final Event_withID eventWithIDDetailFromFeed = new Gson().fromJson(getIntent().getStringExtra("eventDetails"), Event_withID.class);

        final Event eventDetailFromFeed = eventWithIDDetailFromFeed.getEvent();

        final List<EventDetailsInvitee> inviteesForEvent = new ArrayList<>();

        for(Entry<String, String> invitee : eventDetailFromFeed.getAttendingMap().entrySet()) {
            String userName = invitee.getKey();
            String fullName = invitee.getValue();
            inviteesForEvent.add(new EventDetailsInvitee(userName, fullName.split(" ")[0], fullName.split(" ")[1], true));
        }

        int attendingCount = eventDetailFromFeed.getAttendingMap().size();

        this.totalAcceptedInvitees.setText(String.valueOf(attendingCount) + " yes");

        for(Entry<String, String> invitee : eventDetailFromFeed.getInvitedMap().entrySet()) {
            String userName = invitee.getKey();
            String fullName = invitee.getValue();
            inviteesForEvent.add(new EventDetailsInvitee(userName, fullName.split(" ")[0], fullName.split(" ")[1], false));
        }

        this.totalInvitees.setText(String.valueOf(eventDetailFromFeed.getInvitedMap().size() + attendingCount) + " guests");

        this.myAdapter = new EventHomeFeedClickedInviteesRecyclerViewAdapter(inviteesForEvent);

        this.eventTitle.setText(eventDetailFromFeed.getTitle());

        this.eventLocation.setText(eventDetailFromFeed.getLocation());

        this.eventDate.setText(eventDetailFromFeed.getEventDate() + " " + eventDetailFromFeed.getEventTime());

        this.eventOrganizer.setText(eventDetailFromFeed.getOrganizer());

        this.eventDescription.setText(eventDetailFromFeed.getDescription());

        this.eventGuests.setAdapter(this.myAdapter);

        this.closeScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });

        this.deleteEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                //get Current Username of device
                String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail()
                    .split("@weout.com")[0];

                WriteBatch batchDeleteEvent = dbReference.batch();

                //if we are the organizer for the event and delete, need to remove from the rest of
                //the invitees
                if (userName.equals(eventDetailFromFeed.getOrganizer())) {


                    //delete 'attendingMap' field from document with corresponding event ID
                    HashMap<String, Object> deleteAttendingInvitees = new HashMap<>();
                    deleteAttendingInvitees.put("attendingMap", FieldValue.delete());

                    DocumentReference currentEventAttendingRef = dbReference.collection("events")
                        .document(eventWithIDDetailFromFeed.getEventID());
                    batchDeleteEvent.update(currentEventAttendingRef, deleteAttendingInvitees);

                    //delete 'invitedMap' field from document with corresponding event ID
                    HashMap<String, Object> deleteInvitedGuests = new HashMap<>();
                    deleteAttendingInvitees.put("invitedMap", FieldValue.delete());

                    DocumentReference currentEventInvitedRef = dbReference.collection("events")
                        .document(eventWithIDDetailFromFeed.getEventID());
                    batchDeleteEvent.update(currentEventInvitedRef, deleteInvitedGuests);

                    //now delete the event id document
                    DocumentReference currentEventRef = dbReference.collection("events")
                        .document(eventWithIDDetailFromFeed.getEventID());
                    batchDeleteEvent.delete(currentEventRef);

                    //now we need to delete the event from each users invited and accepted document
                    HashMap<String, Object> deleteInviteInUserInvites = new HashMap<>();
                    deleteInviteInUserInvites.put(eventWithIDDetailFromFeed.getEventID(), FieldValue.delete());

                    //traverse each invited user and delete from their event invites
                    for(String username : eventDetailFromFeed.getInvitedMap().keySet()) {

                            batchDeleteEvent.update(dbReference.collection("users")
                                .document(username).collection("events")
                                .document("invited"), deleteInviteInUserInvites);
                    }

                    //traverse each invited user and delete from their event home feed
                    for(String username : eventDetailFromFeed.getAttendingMap().keySet()) {

                        batchDeleteEvent.update(dbReference.collection("users")
                            .document(username).collection("events")
                            .document("accepted"), deleteInviteInUserInvites);
                    }

                    batchDeleteEvent.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully Deleted Event: " + eventWithIDDetailFromFeed.getEventID());
                            Utilities.displaySnackBar(view, getApplicationContext(), "Event Deleted", R.color.lightBlue);
                            // Wait a bit to finish this activity
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    finish();
                                }
                            }, 1500);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Unable to delete Event: " + eventWithIDDetailFromFeed.getEventID());
                            Utilities.displaySnackBar(view, getApplicationContext(), "Error: Can't delete Event at this time", R.color.lightBlue);
                        }
                    });

                }
                //we need to delete the event from the user's events collection under the accepted document and we need to delete the event
                //from the event's attendingMap
                else {

                    //create a reference to the user's events collection under the accepted document
                    DocumentReference dbUserAcceptedRef = dbReference.collection("users")
                        .document(userName).collection("events").document("accepted");

                    //delete the eventID from the user's accepted document
                    HashMap<String, Object> deleteAcceptedEvent = new HashMap<>();
                    deleteAcceptedEvent.put(eventWithIDDetailFromFeed.getEventID(), FieldValue.delete());

                    batchDeleteEvent.update(dbUserAcceptedRef, deleteAcceptedEvent);

                    //create a reference to the event's attendingMap Field
                    DocumentReference dbEventAttending = dbReference.collection("events")
                        .document(eventWithIDDetailFromFeed.getEventID());

                    //delete the username key from the attendingMap Map
                    HashMap<String, Object> deleteUsernameFromAttendingMap = new HashMap<>();
                    deleteUsernameFromAttendingMap.put("attendingMap." + userName, FieldValue.delete());

                    batchDeleteEvent.update(dbEventAttending,deleteUsernameFromAttendingMap);

                    batchDeleteEvent.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Successfully Deleted Event: " + eventWithIDDetailFromFeed.getEventID());
                            Utilities.displaySnackBar(view, getApplicationContext(), "Event Deleted", R.color.lightBlue);
                            // Wait a bit to finish this activity
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    finish();
                                }
                            }, 1500);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Unable to delete Event: " + eventWithIDDetailFromFeed.getEventID());
                            Utilities.displaySnackBar(view, getApplicationContext(), "Error: Can't delete Event at this time", R.color.lightBlue);
                        }
                    });
                }

            }
        });

    }
}
