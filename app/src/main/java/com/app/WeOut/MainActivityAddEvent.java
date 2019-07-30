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

import com.app.WeOut.ToDelete.MainActivityHomeInviteFriends;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import utils.Event;
import utils.Utilities;
import utils.CustomSnackBar;

public class MainActivityAddEvent extends AppCompatActivity {

    // Declare variables for XML components
    Button btn_DatePicker, btn_TimePicker;
    Button btn_InviteFriends;
    TextView textView_Date, textView_Time;
    EditText eventCreationDescription, eventCreationTitle, eventCreationLocation;

    // Private variables
    private String TAG = "EventCreation: ";
    private int _year, _month, _day, _hour, _minute;
    private int currentYear, currentMonth, currentDay, currentHour, currentMinute;
    private CustomSnackBar customSnackBar = new CustomSnackBar();

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

        // Check for issues in the fields
        int checkFieldsResult = checkFields();
        if (checkFieldsResult != 0) {
            String errorMessage = "Error: Incorrect Fields";

            if (checkFieldsResult == 1) {
                errorMessage = "Error: Fields are empty.";
            }
            else if (checkFieldsResult == 2) {
                errorMessage = "Error: Your event occurs in the past.";
            }

            customSnackBar.display(view, getApplicationContext(), errorMessage);
            return;
        }

        // Get current user's username
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = email.substring(0, email.indexOf("@weout.com"));

        // Create intent to switch to new activity to invite friends
        Intent intent = new Intent(MainActivityAddEvent.this, MainActivityHomeInviteFriends.class);

        // Create an event object with the received information
        Event event = new Event(
                this.eventCreationTitle.getText().toString(),
                this.eventCreationLocation.getText().toString(),
                this.textView_Date.getText().toString(),
                this.textView_Time.getText().toString(),
                String.valueOf(new Timestamp(new Date()).getSeconds()),
                this.eventCreationDescription.getText().toString(),
                username
        );

        // Convert the event information into a JSON
        Gson gson = new Gson();
        String eventJson = gson.toJson(event);
        // Log json for debugging
        Log.d(TAG, eventJson);

        // Attach the JSON as a string to the intent
        intent.putExtra("newEventJson", eventJson);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

        // Switch to new activity
        startActivity(intent);
        finish();
    }

    // Check the fields given to the event creation activity
    // TODO: Add enums for result values
    private int checkFields() {
        int result = 0;

        // Check each field to see if it's empty first
        if (Utilities.isEmpty(eventCreationTitle)) {
            result = 1;
        }
        else if (Utilities.isEmpty(eventCreationLocation)) {
            result = 1;
        }
        else if (Utilities.isEmpty(eventCreationDescription)) {
            result = 1;
        }
        else if (Utilities.isEmpty(textView_Date)) {
            result = 1;
        }
        else if (Utilities.isEmpty(textView_Time)) {
            result = 1;
        }

        // Can add checking for date/time conflict or past date time here.
        else if (Utilities.isToday(_year, _month, _day) && Utilities.isPastCurrentTime(_hour, _minute)) {
            result = 2;
        }

        return result;
    }

    // Function called when Date Picker button is clicked
    public void onClick_DatePicker(View view) {
        Log.d(TAG, "Date Picker Clicked");

        Calendar cal = Calendar.getInstance();
        this.currentYear = cal.get(Calendar.YEAR);
        this.currentMonth = cal.get(Calendar.MONTH);
        this.currentDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        textView_Date.setText((month+1) + "-" + (day) + "-" + year);
                        _year = year;
                        _month = month+1;
                        _day = day;
                    }

                }, this.currentYear, this.currentMonth, this.currentDay);

        // Make sure you can only select future dates.
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    // Function called when Time Picker button is clicked
    public void onClick_TimePicker(View view) {
        Log.d(TAG, "Time Picker Clicked");

        // Get Current Time
        Calendar c = Calendar.getInstance();
        this.currentHour = c.get(Calendar.HOUR_OF_DAY);
        this.currentMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        textView_Time.setText(convertTimeToString(hour, minute));
                        _hour = hour;
                        _minute = minute;
                    }

                }, this.currentHour, this.currentMinute, false);

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

