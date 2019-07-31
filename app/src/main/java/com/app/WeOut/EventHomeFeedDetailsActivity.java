package com.app.WeOut;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 * EventHomeFeedDetails Activity is a class that extends{@link AppCompatActivity} and is focused on
 * displaying detail such as title, location, date, and attendees for an Event.
 */
public class EventHomeFeedDetailsActivity extends AppCompatActivity {
    private TextView eventTitle, eventLocation, eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_home_feed_details);
        this.eventTitle = findViewById(R.id.eventHomeFeedClickedTitle);
        this.eventLocation = findViewById(R.id.eventHomeFeedClickedLocation);
        this.eventDate = findViewById(R.id.eventHomeFeedClickedDate);


    }
}
