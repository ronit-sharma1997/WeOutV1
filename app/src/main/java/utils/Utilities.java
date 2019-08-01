package utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.WeOut.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Utilities {

    private static String TAG = "Utilities";

    public static boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isEmpty(TextView textView) {
        if (textView.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static boolean isPastCurrentDay(int yearSelected, int monthSelected, int daySelected) {
        boolean result = false;

        int currYear, currMonth, currDay;

        Calendar cal = Calendar.getInstance();

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        if (currYear > yearSelected) {
            result = true;
        }
        else if (currMonth > monthSelected) {
            result = true;
        }
        else if (currDay > daySelected) {
            result = true;
        }

        return result;
    }

    public static boolean isToday(int yearSelected, int monthSelected, int daySelected) {
        boolean result = false;

        int currYear, currMonth, currDay;

        Calendar cal = Calendar.getInstance();

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH) + 1;
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        Log.d(TAG, "Date Selected: " + yearSelected + "-" + monthSelected + "-" + daySelected);
        Log.d(TAG, "Curr Date: " + currYear + "-" + currMonth + "-" + currDay);

        if (currYear == yearSelected && currMonth == monthSelected && currDay == daySelected) {
            result = true;
        }

        return result;
    }

    public static boolean isPastCurrentTime(int hourSelected, int minuteSelected) {

        boolean result = false;
        boolean isAM;

        int currHour, currMinute;

        Calendar cal = Calendar.getInstance();

        currHour = cal.get(Calendar.HOUR);
        currMinute = cal.get(Calendar.MINUTE);

        isAM = (cal.get(Calendar.AM_PM) == Calendar.AM) ? true : false;
        if (!isAM) {
            currHour += 12;
        }

        Log.d(TAG, "Hr selected: " + hourSelected + ", Min selected: " + minuteSelected);
        Log.d(TAG, "Curr Hr: " + currHour + ", Curr Min: " + currMinute);

        if (currHour > hourSelected) {
            result = true;
        }
        else if (currHour == hourSelected && currMinute > minuteSelected) {
            result = true;
        }

        return result;
    }

    public static String getCurrentUsername () {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = email.substring(0, email.indexOf("@weout.com"));

        return username;
    }

    public static void createEventInDatabase(final Event event, final View view, final Context context) {
        // Get database variable instance from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get Document Reference
        DocumentReference df = db.collection("events").document();

        // Store unique event ID
        final String eventID = df.getId();

        df.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("EventCreation: ", "Successful. Event ID: " + eventID);

                // Add the event invitation to every user event collection
                addEventToUserEventsCollection(event, eventID, view, context);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EventCreation: ", e.getMessage());
                CustomSnackBar customSnackBar = new CustomSnackBar();
                customSnackBar.display(view, context, "Event creation in the database has failed.");
            }
        });
    }

    private static void addEventToUserEventsCollection(Event event, String documentID, final View view, final Context context) {
        // First add the event to the organizer's accepted list
        // Get database variable instance from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get Document Reference for organizer
        DocumentReference df_organizer = db
                .collection("users").document(event.getOrganizer())
                .collection("events").document("accepted");

        // Create the event map data object that will be added to each users events collection
        HashMap <String, Object> eventMapData = new HashMap<>();
        eventMapData.put(documentID, true);

        // Create the batch to write all the data at once
        WriteBatch batch = db.batch();

        // Set the organizer's data under accepted
        batch.update(df_organizer, eventMapData);

        // Create an array list to store the invited friend data
        ArrayList <String> invitedFriendList = new ArrayList<String>(event.getInvitedMap().keySet());

        // Loop through every invited user and add them to the batch
        for (int i = 0; i < invitedFriendList.size(); i++) {
            // Get the username of the friend at the current index
            String invitedFriendUsername = invitedFriendList.get(i);

            // Get a document reference for this particular friend's invited list
            DocumentReference df_invitedFriend = db
                    .collection("users").document(invitedFriendUsername)
                    .collection("events").document("invited");

            // Add this value to the batch
            batch.update(df_invitedFriend, eventMapData);
        }

        // Finally, commit the batch
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("EventCreation: ", "Successfully added event to all affected users.");
                CustomSnackBar customSnackBar = new CustomSnackBar();
                customSnackBar.display(view, context, "Event creation successful!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EventCreation: ", "Failed to update all user documents. Error: " + e.getMessage());
                CustomSnackBar customSnackBar = new CustomSnackBar();
                customSnackBar.display(view, context, "Event creation in the database has failed.");
            }
        });

    }

    public static void deleteEventAsOrganizer(final Event_withID eventWithID, final View view, final Context context) {
        // Get database variable instance from firebase
        FirebaseFirestore dbReference = FirebaseFirestore.getInstance();

        //Get Event stored in extended class Event_withID
        Event eventDetail = eventWithID.getEvent();

        WriteBatch batchDeleteEvent = dbReference.batch();

        //delete 'attendingMap' field from document with corresponding event ID
        HashMap<String, Object> deleteAttendingInvitees = new HashMap<>();
        deleteAttendingInvitees.put("attendingMap", FieldValue.delete());

        DocumentReference currentEventAttendingRef = dbReference.collection("events")
            .document(eventWithID.getEventID());
        batchDeleteEvent.update(currentEventAttendingRef, deleteAttendingInvitees);

        //delete 'invitedMap' field from document with corresponding event ID
        HashMap<String, Object> deleteInvitedGuests = new HashMap<>();
        deleteAttendingInvitees.put("invitedMap", FieldValue.delete());

        DocumentReference currentEventInvitedRef = dbReference.collection("events")
            .document(eventWithID.getEventID());
        batchDeleteEvent.update(currentEventInvitedRef, deleteInvitedGuests);

        //now delete the event id document
        DocumentReference currentEventRef = dbReference.collection("events")
            .document(eventWithID.getEventID());
        batchDeleteEvent.delete(currentEventRef);

        //now we need to delete the event from each users invited and accepted document
        HashMap<String, Object> deleteInviteInUserInvites = new HashMap<>();
        deleteInviteInUserInvites.put(eventWithID.getEventID(), FieldValue.delete());

        //traverse each invited user and delete from their event invites
        for(String username : eventDetail.getInvitedMap().keySet()) {

            batchDeleteEvent.update(dbReference.collection("users")
                .document(username).collection("events")
                .document("invited"), deleteInviteInUserInvites);
        }

        //traverse each invited user and delete from their event home feed
        for(String username : eventDetail.getAttendingMap().keySet()) {

            batchDeleteEvent.update(dbReference.collection("users")
                .document(username).collection("events")
                .document("accepted"), deleteInviteInUserInvites);
        }

        batchDeleteEvent.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully Deleted Event: " + eventWithID.getEventID());
                Utilities.displaySnackBar(view, context, "Event Deleted", R.color.lightBlue);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to delete Event: " + eventWithID.getEventID());
                Utilities.displaySnackBar(view, context, "Error: Can't delete Event at this time", R.color.lightBlue);
                throw new IllegalStateException("Error deleting event from database");
            }
        });

    }

    public static void deleteEventNonOrganizer(final Event_withID eventDetails, final View view, final Context context) {
        // Get database variable instance from firebase
        FirebaseFirestore dbReference = FirebaseFirestore.getInstance();

        WriteBatch batchDeleteEvent = dbReference.batch();

        String currentUsername = getCurrentUsername();

        //create a reference to the user's events collection under the accepted document
        DocumentReference dbUserAcceptedRef = dbReference.collection("users")
            .document(currentUsername).collection("events").document("accepted");

        //delete the eventID from the user's accepted document
        HashMap<String, Object> deleteAcceptedEvent = new HashMap<>();
        deleteAcceptedEvent.put(eventDetails.getEventID(), FieldValue.delete());

        batchDeleteEvent.update(dbUserAcceptedRef, deleteAcceptedEvent);

        //create a reference to the event's attendingMap Field
        DocumentReference dbEventAttending = dbReference.collection("events")
            .document(eventDetails.getEventID());

        //delete the username key from the attendingMap Map
        HashMap<String, Object> deleteUsernameFromAttendingMap = new HashMap<>();
        deleteUsernameFromAttendingMap.put("attendingMap." + currentUsername, FieldValue.delete());

        batchDeleteEvent.update(dbEventAttending,deleteUsernameFromAttendingMap);

        batchDeleteEvent.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully Deleted Event: " + eventDetails.getEventID());
                Utilities.displaySnackBar(view, context, "Event Deleted", R.color.lightBlue);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Unable to delete Event: " + eventDetails.getEventID());
                Utilities.displaySnackBar(view, context, "Error: Can't delete Event at this time", R.color.lightBlue);
                throw new IllegalStateException("Error with deleting the event");
            }
        });
    }

    public static void displaySnackBar (View view, Context context, String message) {
        // Create a new custom snack bar
        CustomSnackBar customSnackBar = new CustomSnackBar();

        customSnackBar.display(view, context, message);
    }

    public static void displaySnackBar (View view, Context context, String message, int colorID) {
        // Create a new custom snack bar
        CustomSnackBar customSnackBar = new CustomSnackBar();

        // Display the snackbar
        customSnackBar.display(view, context, message, colorID);
    }

}
