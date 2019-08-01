package com.app.WeOut;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import java.util.Calendar;
import java.util.Date;
import utils.CustomSnackBar;
import utils.Event;
import utils.Utilities;


/**
 * MainActivityAddEventFragment extends {@link Fragment} subclass and its focus is for displaying
 * textboxes for event information for the user to fill out.
 */
public class MainActivityAddEventFragment extends Fragment {

  private Button btn_DatePicker, btn_TimePicker;
  private TextView textView_Date, textView_Time;
  private EditText eventCreationDescription, eventCreationTitle, eventCreationLocation;
  private FloatingActionButton addEventFAB;
  private final static int ANIMATION_DURATION = 150;
  private CardView cardView;
  private FrameLayout createEventContainer;
  private ImageButton closeButton;
  private Button inviteFriends;
  String TAG = "MainActivityAddEventFragment";
  private float fabOriginX;
  private float fabOriginY;
  private int _year, _month, _day, _hour, _minute;
  private int currentYear, currentMonth, currentDay, currentHour, currentMinute;
  private CustomSnackBar customSnackBar = new CustomSnackBar();
  private Bundle savedState = null;

  public MainActivityAddEventFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View view = inflater.inflate(R.layout.fragment_main_activity_add_event, container, false);
    this.cardView = view.findViewById(R.id.createEventCardView);
    this.createEventContainer = getActivity().findViewById(R.id.createEventScreen);
    //animate the Card View so it appears, scaling both the width and height by 1 for ANIMATION_DURATION
    this.cardView.animate()
        .scaleX(1).scaleY(1)
        .setDuration(ANIMATION_DURATION).start();
    this.closeButton = view.findViewById(R.id.closeScreenButton);
    this.inviteFriends = view.findViewById(R.id.eventCreationInviteFriendsButton);

    //Add Listener for Clicking on Invite Friends Button
    this.inviteFriends.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Clicked on Invite Friends");
        onClick_InviteFriends(view);
      }
    });

    //Add Listener for Close Screen Button
    this.closeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Clicked on Close Screen");
        closeScreen();
      }
    });

    // Variables for Setting Date and Time
    eventCreationTitle = view.findViewById(R.id.eventCreationTitleEditText);
    eventCreationLocation = view.findViewById(R.id.eventCreationLocationEditText);
    btn_DatePicker = view.findViewById(R.id.eventCreationSetDateButton);
    btn_DatePicker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onClick_DatePicker();
      }
    });
    btn_TimePicker = view.findViewById(R.id.eventCreationSetTimeButton);
    btn_TimePicker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onClick_TimePicker();
      }
    });
    textView_Date = view.findViewById(R.id.eventCreationDateTextView);
    textView_Time = view.findViewById(R.id.eventCreationTimeTextView);

    eventCreationDescription = view.findViewById(R.id.eventCreationDescriptionTextInputEditText);
    eventCreationDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

    if(savedInstanceState != null && savedState == null) {
      this.savedState = savedInstanceState.getBundle("storedBundle");
    }
    if(savedState != null) {
        this.textView_Time.setText(savedState.getString("eventTime"));
        this.textView_Date.setText(savedState.getString("eventDate"));
        this.eventCreationLocation.setText(savedState.getString("eventLocation"));
        this.eventCreationDescription.setText(savedState.getString("eventDescription"));
        this.eventCreationTitle.setText(savedState.getString("eventTitle"));
    }

    return view;
  }

  /**
   * Helper function to complete the process of Closing the current screen.
   */
  private void closeScreen() {
    //animate the card view so that it disappears in duration ANIMATION_DURATION
    ViewPropertyAnimator animator = this.cardView.animate()
        .scaleX(0).scaleY(0)
        .setDuration(ANIMATION_DURATION);
    animator.start();

    //animate the container that holds the card view so that it disappears in duration ANIMATION_DURATION
    createEventContainer.animate().scaleX(0).scaleY(0).setDuration(ANIMATION_DURATION)
        .setStartDelay(50).setListener(new AnimatorListenerAdapter() {
    //on the animation ending we hide the container view to display the home feed
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
   * Helper function to rotate the card views so that the Invite Friend List appears.
   */
  private void switchCardViews(Bundle bundle) {
    MainActivityAddEventInviteFriendsFragment fragment = new MainActivityAddEventInviteFriendsFragment();
    fragment.setArguments(bundle);
    //pass the floating action button pointer to the next fragment along with its original x and y coordinates
    fragment.setAddEventFAB(this.addEventFAB, this.fabOriginX, this.fabOriginY);
    //set a custom animation to transition from this fragment to
    // MainActivityAddEventInviteFriendsFragment and start the transaction
    getFragmentManager().beginTransaction()
        .setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out)
        .replace(R.id.createEventScreen, fragment).commit();

  }

  private void onClick_InviteFriends(View view) {
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

      customSnackBar.display(view, getActivity(), errorMessage);
      return;
    }

    // Get current user's username
    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    String username = email.substring(0, email.indexOf("@weout.com"));


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

    //Create Bundle to pass an argument to the next fragment
    Bundle bundle = new Bundle();

    // Attach the JSON as a string to the Bundle
    bundle.putString("newEventJson", eventJson);

    switchCardViews(bundle);
  }

  /**
   * Method to set a reference to the add event floating action button
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
   * Helper Function called when Date Picker button is clicked.
   */
  private void onClick_DatePicker() {
    Log.d(TAG, "Date Picker Clicked");

    Calendar cal = Calendar.getInstance();
    this.currentYear = cal.get(Calendar.YEAR);
    this.currentMonth = cal.get(Calendar.MONTH);
    this.currentDay = cal.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),R.style.DialogTheme,
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

  /**
   * Helper Function called when Time Picker button is clicked.
   */
  private void onClick_TimePicker() {
    Log.d(TAG, "Time Picker Clicked");

    // Get Current Time
    Calendar c = Calendar.getInstance();
    this.currentHour = c.get(Calendar.HOUR_OF_DAY);
    this.currentMinute = c.get(Calendar.MINUTE);

    // Launch Time Picker Dialog
    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme,
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

  /**
   * Helper function to convert time from 24 hour time to 12 hour time with AM/PM
   * @param hour - hour value of time
   * @param minute - minute value of time
   * @return String representing the time
   */
  private String convertTimeToString(int hour, int minute) {
    String returnTimeString = "00:00 AM";
    String amOrPm = "AM";
    if (hour == 0) {
      hour = 12;
    } else if (hour >= 12) {
      amOrPm = "PM";
      if (hour >= 13) {
        hour = hour - 12;
      }
    }

    returnTimeString =
        String.format("%02d", hour) + ":" + String.format("%02d", minute) + " " + amOrPm;
    return returnTimeString;
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

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBundle("storedBundle", (savedState != null) ? savedState : saveState());

  }
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    savedState = saveState();

  }

  private Bundle saveState() {
    Bundle state = new Bundle();
    state.putString("eventTime", this.textView_Time.getText().toString());
    state.putString("eventDate", this.textView_Date.getText().toString());
    state.putString("eventLocation", this.eventCreationLocation.getText().toString());
    state.putString("eventDescription", this.eventCreationDescription.getText().toString());
    state.putString("eventTitle", this.eventCreationTitle.getText().toString());

    return state;
  }


}
