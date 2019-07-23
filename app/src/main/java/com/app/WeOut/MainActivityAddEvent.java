package com.app.WeOut;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.Calendar;

import utils.Event;

public class MainActivityAddEvent extends AppCompatActivity {
    Button btn_DatePicker, btn_TimePicker;
    Button btn_InviteFriends;
    TextView textView_Date, textView_Time;
    EditText eventCreationDescription, eventCreationTitle, eventCreationLocation;

    String TAG = "EventCreation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_event);

        // Variables for Setting Date and Time
        eventCreationTitle = findViewById(R.id.eventCreationTitleEditText);
        eventCreationLocation = findViewById(R.id.eventCreationLocationEditText);
        btn_DatePicker = findViewById(R.id.eventCreationSetDateButton);
        btn_TimePicker = findViewById(R.id.eventCreationSetTimeButton);
        textView_Date = findViewById(R.id.eventCreationDateTextView);
        textView_Time = findViewById(R.id.eventCreationTimeTextView);

        eventCreationDescription = findViewById(R.id.eventCreationDescriptionTextInputEditText);
        eventCreationDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);
        btn_InviteFriends = findViewById(R.id.eventCreationInviteFriendsButton);
    }

    public void onClick_InviteFriends(View view) {
        Log.d(TAG, "Invite Friends Clicked");
        String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String shortenUserName = userName.substring(0, userName.indexOf("@weout.com"));

        // Create intent to switch to new activity to invite friends
        Intent intent = new Intent(MainActivityAddEvent.this, MainActivityHomeInviteFriends.class);
        Event newEvent = new Event(this.eventCreationTitle.getText().toString(),
                this.textView_Date.getText().toString() + " " + this.textView_Time.getText()
                        .toString(),shortenUserName, this.eventCreationLocation.getText().toString(),
                this.eventCreationDescription.getText().toString());

        Gson gson = new Gson();
        String object = gson.toJson(newEvent);
        intent.putExtra("newEvent", object);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        // Switch to new activity
        startActivity(intent);
        finish();

    }

    // Function called when Date Picker button is clicked
    public void onClick_DatePicker(View view) {
        Log.d(TAG, "Date Picker Clicked");

        int currentYear, currentMonth, currentDay;

        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH);
        currentDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        textView_Date.setText((month+1) + "-" + (day) + "-" + year);
                    }
                }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    // Function called when Time Picker button is clicked
    public void onClick_TimePicker(View view) {
        Log.d(TAG, "Time Picker Clicked");

        int currentHour, currentMinute;

        // Get Current Time
        Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {

                        textView_Time.setText(convertTimeToString(hour, minute));
                    }
                }, currentHour, currentMinute, false);
        timePickerDialog.show();
    }

    // Helper function to convert time from 24 hour time to 12 hour time with AM/PM
    private String convertTimeToString(int hour, int minute) {
        String returnTimeString = "00:00 AM";
        String amOrPm = "AM";
        if(hour == 0) {
            hour = 12;
        }

        else if (hour >= 12) {
            amOrPm = "PM";
            if (hour >= 13) {
                hour = hour - 12;
            }
        }

        returnTimeString = String.format("%02d", hour) + ":" + String.format("%02d", minute) + " " + amOrPm;
        return returnTimeString;
    }

}

