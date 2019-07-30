package utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.WeOut.R;

import java.util.ArrayList;
import java.util.Set;

import com.app.WeOut.MainActivityHomeFragment.OnListFragmentInteractionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventHomeFeedRecyclerViewAdapter extends RecyclerView.Adapter<EventHomeFeedRecyclerViewAdapter.ViewHolder> {

    // Arraylist to store events
    private ArrayList<Event_withID> eventsList;

    private final OnListFragmentInteractionListener mListener;

    // Private variables
    private String username;
    private String TAG;
    private TextView emptyListTextView;
    // Instance of DB
    FirebaseFirestore db;

    public EventHomeFeedRecyclerViewAdapter(
            ArrayList<Event_withID> items,
            OnListFragmentInteractionListener listener,
            TextView emptyListTextView) {

        this.eventsList = items;
        this.emptyListTextView = emptyListTextView;
        mListener = listener;

        // Get current user's information from firebase
        this.username = Utilities.getCurrentUsername();

        // Get instance of database
        db = FirebaseFirestore.getInstance();

        // Set up a listener to wait for changes to the user's accepted events
        setEventListener();

        // Set TAG
        TAG = "EventHomeFeedRecyclerViewAdapter";
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_event_invent, parent, false);

        return new ViewHolder(view);
    }

    private void setEventListener() {
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        // Listen for accepted event changes
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        // Get user's accepted events
        DocumentReference df_acceptedEvents = db
                .collection("users").document(username)
                .collection("events").document("accepted");

        // Add a snapshot listener to listen for changes to the user's document
        df_acceptedEvents.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                // If we have some sort of error
                if(e != null) {
                    Log.d(TAG, e.getMessage());
                    return;
                }

                // Clear the events list because we are about to add events again
//                eventsList.clear();

                Log.d(TAG, "Accepted Event Snapshot Listener");

                // Else, if the data exists
                if(documentSnapshot.exists() && documentSnapshot != null) {

                    // Create a set of all the event ID's the user has accepted
                    Set<String> eventID_Set = documentSnapshot.getData().keySet();
//                    ArrayList <String> eventID_List = new ArrayList<>(documentSnapshot.getData().keySet());
                    Log.d(TAG, "Events from DB: " + documentSnapshot.getData().keySet().toString());

                    // Delete the fake event if it exists
                    if (eventID_Set.contains("FakeEvent")) { eventID_Set.remove("FakeEvent"); }
                    Log.d(TAG, "New Set with FakeEvent Removed:" + eventID_Set.toString());

                    // If the user doesn't have any accepted events
                    if (eventID_Set.size() == 0) {
                        Log.d(TAG, "No accepted events.");
                        eventsList.clear();
                        emptyListTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                    // Else if I have an event locally that I do not have on the database
                    // (In other words, if an event was deleted from the database)
                    // Then clear the events list and the code after should re-populate it.
                    else if (eventsList.size() > eventID_Set.size()) {
                        Log.d(TAG, "Clearing event list to repopulate.");
                        eventsList.clear();
                    }

                    // Set the empty list text view as gone
                    emptyListTextView.setVisibility(View.GONE);

                    // Get only the Set of events that the user does NOT have
                    for (int i = 0; i < eventsList.size(); i++) {
                        // Retrieve the event ID in the list at the current index
                        String eventID = eventsList.get(i).getEventID();

                        // If the event ID is in the events list already, remove it from the set
                        if (eventID_Set.contains(eventID)) {
                            eventID_Set.remove(eventID);
                        }
                    }

                    // Calculate expected event list size so I know when to notify the adapter
                    // of a changed data set.
                    final int expectedEventListSize = eventsList.size() + eventID_Set.size();
                    Log.d(TAG, "ExpectedEventListSize: " + expectedEventListSize);
                    Log.d(TAG, "EventID_Set Size: " + eventID_Set.size());

                    // For every event in the accepted set
                    for (String eventID : eventID_Set) {

                        // Create a reference for this current event object
                        final DocumentReference df_event = db
                                .collection("events").document(eventID);

                        // Get the event object from the database
                        df_event.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists() && documentSnapshot != null) {

                                    Log.d(TAG, "#" + eventsList.size() + " :" + "Adding Doc ID: " + df_event.getId());

                                    // Add the event object to the events list
                                    eventsList.add(
                                            new Event_withID
                                                    (
                                                    documentSnapshot.toObject(Event.class),
                                                    df_event.getId()
                                                    )
                                    );

                                    if (expectedEventListSize == eventsList.size()) {
                                        Log.d(TAG, "Event List Size: " + eventsList.size() + " -> notifying change");
                                        notifyDataSetChanged();
                                    }

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed to retrieve " + df_event.getId() + ": " + e.getMessage());
                            }
                        });

//                        if (expectedEventListSize == eventsList.size()) {
//                            Log.d(TAG, "Event List Size: " + eventsList.size() + " -> notifying change");
//                            notifyDataSetChanged();
//                        }

                    }  // End of iterating through the event ID list

                    // Set the visibility of the text view if the events list is empty
//                    emptyListTextView.setVisibility(eventsList.isEmpty() ? View.VISIBLE : View.GONE);

                    Log.d(TAG, "Events List Size: " + eventsList.size());

                    // Notify the adapter that the data set has changed
                    // notifyDataSetChanged();

                } // End of document snapshot for event ID list

            }
        }); // end of snapshot listener
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Log.d(TAG, "OnBindViewHolder");

        // fill the holder with information for the corresponding event
        holder.event_withID_Object = this.eventsList.get(position);

        // Set visible data based on event information
        holder.eventTitle.setText(holder.event_withID_Object.getEvent().getTitle());
        holder.eventLocation.setText(holder.event_withID_Object.getEvent().getLocation());
        holder.eventDate.setText(
                holder.event_withID_Object.getEvent().getEventDate() + " " +
                holder.event_withID_Object.getEvent().getEventTime()
        );

        // Only show the organizer badge if this event was created by the current user
        holder.organizer.setVisibility(View.GONE);
        if(this.username.equals(holder.event_withID_Object.getEvent().getOrganizer())) {
            holder.organizer.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement switching activity to view more details about the event here

//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        // Components associated with XML
        public final TextView eventTitle;
        public final TextView eventDate;
        public final TextView eventLocation;
        public final FrameLayout organizer;

        // Event object information
        public Event_withID event_withID_Object;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            eventTitle = view.findViewById(R.id.eventInviteHomeFeedTitle);
            eventDate = view.findViewById(R.id.eventInviteHomeFeedDateTime);
            eventLocation = view.findViewById(R.id.eventInviteHomeFeedLocation);
            organizer = view.findViewById(R.id.organizerBadge);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + eventTitle.getText() + "'";
        }
    }
}
