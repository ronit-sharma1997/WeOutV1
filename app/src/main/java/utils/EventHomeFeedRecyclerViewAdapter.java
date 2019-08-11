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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * Class that extends the {@link RecyclerView.Adapter} for configuration with the events a user has accepted and is displayed on their home feed
 * and makes a call to the specified {@link OnListFragmentInteractionListener}.
 */
public class EventHomeFeedRecyclerViewAdapter extends RecyclerView.Adapter<EventHomeFeedRecyclerViewAdapter.ViewHolder> {

    // Arraylist to store events
    private ArrayList<Event_withID> eventsList;
    private final OnListFragmentInteractionListener mListener;

    // Private variables
    private String username;
    private String TAG;
    private TextView emptyListTextView;

    // FINAL VARIABLES FOR EVENT TEXT SIZE TO DISPLAY
    private final int EVENT_TITLE_MAX_LENGTH = 30;
    private final int EVENT_LOCATION_MAX_LENGTH = 40;

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
                        notifyDataSetChanged();
                        return;
                    }

                    // Set the empty list text view as gone because there must be events that the
                    // user accepts or owns.
                    emptyListTextView.setVisibility(View.GONE);

                    ArrayList <Integer> eventIndiciesToRemove = new ArrayList<>();

                    // Get only the Set of events that the user does NOT have
                    // AND get an arraylist of indicies of events that the local list contains
                    //      but that the database does not contain (ie: event deleted).
                    for (int i = 0; i < eventsList.size(); i++) {
                        // Retrieve the event ID in the list at the current index
                        String eventID = eventsList.get(i).getEventID();

                        // If the event ID is in the events list already, remove it from the set
                        if (eventID_Set.contains(eventID)) {
                            eventID_Set.remove(eventID);
                        }
                        // Else, add it to the list of indicies to be removed
                        // Because it exists locally but not in the database
                        else {
                            eventIndiciesToRemove.add(i);
                        }
                    }

                    // Now, loop through event indicies to remove and remove them
                    for (int i = 0; i < eventIndiciesToRemove.size(); i++) {
                        eventsList.remove(eventIndiciesToRemove.get(i));
                    }

                    // Calculate expected event list size so I know when to notify the adapter
                    // of a changed data set.
                    final int expectedEventListSize = eventsList.size() + eventID_Set.size();
                    Log.d(TAG, "ExpectedEventListSize: " + expectedEventListSize);
                    Log.d(TAG, "Current events list size: " + eventsList.size());
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

                                    sortEvents();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed to retrieve " + df_event.getId() + ": " + e.getMessage());
                            }
                        });

                    }  // End of iterating through the event ID list

                    Log.d(TAG, "Events List Size: " + eventsList.size());



                } // End of document snapshot for event ID list
            }

        }); // end of snapshot listener
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Log.d(TAG, "OnBindViewHolder");

        // fill the holder with information for the corresponding event
        holder.event_withID_Object = this.eventsList.get(position);
        Event event = holder.event_withID_Object.getEvent();

        // Set visible data based on event information
        if (event.getTitle().length() > EVENT_TITLE_MAX_LENGTH) {
            holder.eventTitle.setText(event.getTitle().substring(0, EVENT_TITLE_MAX_LENGTH) + "...");
        } else {
            holder.eventTitle.setText(event.getTitle());
        }

        if (event.getTitle().length() > EVENT_LOCATION_MAX_LENGTH) {
            holder.eventLocation.setText(event.getLocation().substring(0, EVENT_LOCATION_MAX_LENGTH) + "...");
        } else {
            holder.eventLocation.setText(event.getLocation());
        }

        holder.eventDate.setText(event.getEventDate() + " " + event.getEventTime());

        // Only show the organizer badge if this event was created by the current user
        holder.organizer.setVisibility(View.GONE);
        if(this.username.equals(event.getOrganizer())) {
            holder.organizer.setVisibility(View.VISIBLE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.event_withID_Object, v, "acceptedEvents");
                }
            }
        });

    }

    // Clean all elements of the recycler
    public void clear() {
        eventsList.clear();
        notifyDataSetChanged();
    }

    /**
     * Method to refresh the Event Home Feed
     */
    public void refeshDataSource() {
        // Get user's accepted events
        DocumentReference df_acceptedEvents = db
            .collection("users").document(username)
            .collection("events").document("accepted");

        df_acceptedEvents.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot != null) {
                    Set<String> eventID_Set = documentSnapshot.getData().keySet();

                    // For every event in the accepted set
                    for (String eventID : eventID_Set) {

                        // Create a reference for this current event object
                        final DocumentReference df_event = db
                            .collection("events").document(eventID);

                        // Get the event object from the database
                        df_event.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (documentSnapshot.exists() && documentSnapshot != null) {

                                    Log.d(TAG,
                                        "#" + eventsList.size() + " :" + "Adding Doc ID: " + df_event.getId());

                                    // Add the event object to the events list
                                    eventsList.add(
                                        new Event_withID
                                            (
                                                documentSnapshot.toObject(Event.class),
                                                df_event.getId()
                                            )
                                    );

                                    sortEvents();
                                }
                                notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed to retrieve " + df_event.getId() + ": " + e.getMessage());
                            }
                        });

                    }
                    notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to refresh events home feed: " + e.getMessage());
            }
        });


    }

    /**
     * Helper method to sort events by ascending date/time
     */
    private void sortEvents() {
        //sort the event list when we add a new event. Pass a comparator to handle what defines
        //greater than for Java objects
        Collections.sort(eventsList, new Comparator<Event_withID>() {
            @Override
            public int compare(Event_withID event_withID, Event_withID t1) {
                //sort by event created time
                int result = 0;
                try {
                    result = new SimpleDateFormat("MM-dd-yyyy")
                        .parse(event_withID.getEvent()
                            .getEventDate())
                        .compareTo(new SimpleDateFormat("MM-dd-yyyy")
                            .parse(t1.getEvent().getEventDate()));
                    //if the dates are the same break the tie breaker by time
                    if (result == 0) {
                        result = new SimpleDateFormat("hh:mm a")
                            .parse(event_withID.getEvent()
                                .getEventTime())
                            .compareTo(new SimpleDateFormat("hh:mm a")
                                .parse(t1.getEvent().getEventTime()));
                    }
                    //break tie by title of event
                    if (result == 0) {
                        result = event_withID.getEvent().getTitle().trim()
                            .compareTo(t1.getEvent().getTitle().trim());
                    }

                    //break tie by location of event
                    if (result == 0) {
                        result = event_withID.getEvent().getLocation().trim()
                            .compareTo(t1.getEvent().getLocation().trim());
                    }

                    //last resort break tie by description of event
                    if (result == 0) {
                        result = event_withID.getEvent().getDescription().trim()
                            .compareTo(t1.getEvent().getDescription().trim());
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                return result;
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
