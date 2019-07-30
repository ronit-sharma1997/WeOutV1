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
import java.util.HashMap;
import java.util.List;
import com.app.WeOut.MainActivityHomeFragment.OnListFragmentInteractionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventHomeFeedRecyclerViewAdapter extends RecyclerView.Adapter<EventHomeFeedRecyclerViewAdapter.ViewHolder> {

    // Arraylist to store events
    private ArrayList<Event> eventsList;

    private final OnListFragmentInteractionListener mListener;

    // Private variables
    private String username;
    private String TAG;
    private TextView emptyListTextView;
    // Instance of DB
    FirebaseFirestore db;

    public EventHomeFeedRecyclerViewAdapter(
            ArrayList<Event> items,
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
                eventsList.clear();

                Log.d(TAG, "Accepted Event Snapshot Listener");

                // Else, if the data exists
                if(documentSnapshot.exists() && documentSnapshot != null) {

                    // Create an array list of all the event ID's the user has accepted
                    ArrayList <String> eventID_List = new ArrayList<>(documentSnapshot.getData().keySet());
                    Log.d(TAG, documentSnapshot.getData().keySet().toString());

                    // Delete the fake event if it exists
                    if (eventID_List.contains("FakeEvent")) { eventID_List.remove("FakeEvent"); }
                    Log.d(TAG, "List with FakeEvent Removed:" + eventID_List.toString());

                    // If the user doesn't have any accepted events
                    if (eventID_List.size() == 0) {
                        Log.d(TAG, "Clearing events list and leaving snapshot listener");
                        eventsList.clear();
                        return;
                    }

                    // For every event in the accepted list
                    for (int i = 0; i < eventID_List.size(); i++) {

                        // Create a reference for this current event object
                        final DocumentReference df_event = db
                                .collection("events").document(eventID_List.get(i));

                        // Get the event object from the database
                        df_event.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if(documentSnapshot.exists() && documentSnapshot != null) {

                                    Log.d(TAG, "#" + eventsList.size() + " :" + "Adding Doc ID: " + df_event.getId());

                                    // Add the event object to the events list
                                    eventsList.add(documentSnapshot.toObject(Event.class));
                                    // Add the event id to the view holder
//                                    holder.eventID = df_event.getId();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed to retrieve " + df_event.getId() + ": " + e.getMessage());
                            }
                        });

                        if (i == (eventID_List.size()-1)) {
                            Log.d(TAG, "i: " + i + ", ID_List Size: " + eventID_List.size() + " -> notifying change");
                            notifyDataSetChanged();
                        }

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
        holder.eventObject = this.eventsList.get(position);

        // Set visible data based on event information
        holder.eventTitle.setText(this.eventsList.get(position).getTitle());
        holder.eventLocation.setText(this.eventsList.get(position).getLocation());
        holder.eventDate.setText(
                this.eventsList.get(position).getEventDate() + " " +
                this.eventsList.get(position).getEventTime()
        );

        // Only show the organizer badge if this event was created by the current user
        holder.organizer.setVisibility(View.GONE);
        if(this.username.equals(this.eventsList.get(position).getOrganizer())) {
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

//        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//        // Listen for accepted event changes
//        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//
//        // Get user's accepted events
//        DocumentReference df_acceptedEvents = db
//                .collection("users").document(username)
//                .collection("events").document("accepted");
//
//        // Add a snapshot listener to listen for changes to the user's document
//        df_acceptedEvents.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//
//                // If we have some sort of error
//                if(e != null) {
//                    Log.d(TAG, e.getMessage());
//                    return;
//                }
//
//                // Else, if the data exists
//                if(documentSnapshot.exists() && documentSnapshot != null) {
//
//                    // Create an array list of all the event ID's the user has accepted
//                    ArrayList <String> eventID_List = new ArrayList<>(documentSnapshot.getData().keySet());
//                    Log.d(TAG, documentSnapshot.getData().keySet().toString());
//
//                    // Delete the fake event if it exists
//                    if (eventID_List.contains("FakeEvent")) { eventID_List.remove("FakeEvent"); }
//                    Log.d(TAG, "List with FakeEvent Removed:" + eventID_List.toString());
//
//                    // If the user doesn't have any accepted events
//                    if (eventID_List.size() < 0) {
//                        Log.d(TAG, "Clearing events list and leaving snapshot listener");
//                        eventsList.clear();
//                        return;
//                    }
//
//                    // Clear the events list because we are about to add events again
//                    eventsList.clear();
//
//                    // For every event in the accepted list
//                    for (int i = 0; i < eventID_List.size(); i++) {
//
//                        // Create a reference for this current event object
//                        final DocumentReference df_event = db
//                                .collection("events").document(eventID_List.get(i));
//
//                        // Get the event object from the database
//                        df_event.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                                if(documentSnapshot.exists() && documentSnapshot != null) {
//
//                                    Log.d(TAG, "Doc ID: " + df_event.getId());
//                                    Log.d(TAG, documentSnapshot.getData().keySet().toString());
//
//                                    // Add the event object to the events list
//                                    eventsList.add(documentSnapshot.toObject(Event.class));
//                                    // Add the event id to the view holder
//                                    holder.eventID = df_event.getId();
//                                }
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "Failed to retrieve " + df_event.getId() + ": " + e.getMessage());
//                            }
//                        });
//
//                    }  // End of iterating through the event ID list
//
//                    // Set the visibility of the text view if the events list is empty
////                    emptyListTextView.setVisibility(eventsList.isEmpty() ? View.VISIBLE : View.GONE);
//
//                    // Notify the adapter that the data set has changed
//                    notifyDataSetChanged();
//
//                } // End of document snapshot for event ID list
//
//            }
//        }); // end of snapshot listener

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
        public Event eventObject;
        public String eventID;

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
