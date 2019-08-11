package utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.WeOut.MainActivityNotificationsFragment.OnListFragmentInteractionListener;
import com.app.WeOut.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Class that extends the {@link RecyclerView.Adapter} for configuration with the user's event invites to display on the
 * {@link com.app.WeOut.MainActivityNotificationsFragment} screen and makes a call to the specified {@link OnListFragmentInteractionListener}
 * and {@link AcceptRejectButtonListener}.
 */
public class MyEventInvitesRecyclerViewAdapter extends
    RecyclerView.Adapter<MyEventInvitesRecyclerViewAdapter.ViewHolder> {

  private ArrayList<Event_withID> eventInvites;
  private OnListFragmentInteractionListener myListener;
  private AcceptRejectButtonListener buttonListener;

  // Text view to display when you dont have any invites
  private TextView noEventInvitesTextView;

  // Variables for database and debugging
  private String username;
  FirebaseFirestore db;
  private String TAG;


  public MyEventInvitesRecyclerViewAdapter(ArrayList<Event_withID> items,
      OnListFragmentInteractionListener listener,
      AcceptRejectButtonListener buttonListener, TextView noEventInvitesTextView) {

    this.eventInvites = items;
    this.myListener = listener;
    this.buttonListener = buttonListener;
    this.noEventInvitesTextView = noEventInvitesTextView;

    // Instantiate Database variables
    username = Utilities.getCurrentUsername();
    db = FirebaseFirestore.getInstance();

    // Set tag
    TAG = "EventInvitesRecyclerViewAdapter: ";

    // Set eventInviteChangeListener
    setEventInviteChangeListener();

  }

  private void setEventInviteChangeListener() {

    // Get user's invited events
    DocumentReference df_invitedEvents = db
        .collection("users").document(username)
        .collection("events").document("invited");

    df_invitedEvents.addSnapshotListener(new EventListener<DocumentSnapshot>() {
      @Override
      public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
          @Nullable FirebaseFirestoreException e) {

        // If we have some sort of error
        if (e != null) {
          Log.d(TAG, e.getMessage());
          return;
        }

        Log.d(TAG, "Invited Event Snapshot Listener Start");

        if (documentSnapshot.exists() && documentSnapshot != null) {

          // First, get the set of events the user has been invited to
          Set<String> setOfEventsInvitedTo = documentSnapshot.getData().keySet();

          // Delete the fake event if it exists
          if (setOfEventsInvitedTo.contains("FakeEvent")) {
            setOfEventsInvitedTo.remove("FakeEvent");
          }
          Log.d(TAG, "Event Invited To Set: " + setOfEventsInvitedTo.toString());

          // If there are no events to add to your array list of event objects,
          // then log this and return.
          if (setOfEventsInvitedTo.size() == 0) {
            Log.d(TAG, "No events invited to");
            eventInvites.clear();
            noEventInvitesTextView.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
            return;
          }

          // Initialize an arraylist to store indicies of events that have been removed
          ArrayList<Integer> eventIndiciesToRemove = new ArrayList<>();

          for (int i = 0; i < eventInvites.size(); i++) {

            String eventID = eventInvites.get(i).getEventID();

            // If the set does have the event item at the current index,
            // remove the event item from the set.
            // Else add the index to the array list of event indicies to be removed.
            //      This will provide you with a set that only contains items that
            //      have not yet been added to the event invites list.
            if (setOfEventsInvitedTo.contains(eventID)) {
              setOfEventsInvitedTo.remove(eventID);
            } else {
              eventIndiciesToRemove.add(i);
            }

          } // End of for loop

          Log.d(TAG, "EventInvites List Size before removing events: " + eventInvites.size());
          Log.d(TAG, "EventIndicies to remove" + eventIndiciesToRemove.toString());

          // Now, loop through event indicies to remove and remove them
          for (int i = 0; i < eventIndiciesToRemove.size(); i++) {
            eventInvites.remove(eventIndiciesToRemove.get(i));
          }

          Log.d(TAG, "EventInvites List Size after removing events: " + eventInvites.size());
          Log.d(TAG, "EventIDs to add to eventInvites List: " + setOfEventsInvitedTo.toString());

          final int expectedEventInvitesSize = eventInvites.size() + setOfEventsInvitedTo.size();

          for (String eventID : setOfEventsInvitedTo) {

            final DocumentReference df_eventInfo = db
                .collection("events").document(eventID);

            df_eventInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot != null) {

                  Log.d(TAG,
                      "#" + eventInvites.size() + " :" + "Adding Doc ID: " + df_eventInfo.getId());

                  eventInvites.add(new Event_withID
                      (
                          documentSnapshot.toObject(Event.class),
                          df_eventInfo.getId()
                      )
                  );

                  //sort the event list when we add a new event. Pass a comparator to handle what defines
                  //greater than for Java objects
                  Collections.sort(eventInvites, new Comparator<Event_withID>() {
                    @Override
                    public int compare(Event_withID event_withID, Event_withID t1) {
                      //sort by event created time
                      int result = 0;
                      try {
                        result = new SimpleDateFormat("MM-dd-yyyy").parse(event_withID.getEvent()
                            .getEventDate()).compareTo(new SimpleDateFormat("MM-dd-yyyy")
                            .parse(t1.getEvent().getEventDate()));
                        //if the dates are the same break the tie breaker by time
                        if (result == 0) {
                          result = new SimpleDateFormat("hh:mm a").parse(event_withID.getEvent()
                              .getEventTime()).compareTo(new SimpleDateFormat("hh:mm a")
                              .parse(t1.getEvent().getEventTime()));
                        }
                        //break tie by title of event
                        if (result == 0) {
                          result = event_withID.getEvent().getTitle().trim().compareTo(t1.getEvent().getTitle().trim());
                        }

                        //break tie by location of event
                        if (result == 0) {
                          result = event_withID.getEvent().getLocation().trim().compareTo(t1.getEvent().getLocation().trim());
                        }

                        //last resort break tie by description of event
                        if (result == 0) {
                          result = event_withID.getEvent().getDescription().trim().compareTo(t1.getEvent().getDescription().trim());
                        }
                      } catch (ParseException e1) {
                        e1.printStackTrace();
                      }

                      return result;
                    }
                  });

                  if (expectedEventInvitesSize == eventInvites.size()) {
                    Log.d(TAG,
                        "Event Invites Size: " + eventInvites.size() + " -> notifying change");
                    notifyDataSetChanged();
                    noEventInvitesTextView.setVisibility(View.GONE);
                  }

                }
              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve event invite information: " + e.getMessage());
              }
            });

          }

        }
      }
    });


  }


  @NonNull
  @Override
  public MyEventInvitesRecyclerViewAdapter.ViewHolder onCreateViewHolder(
      @NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.fragment_event, viewGroup, false);

    return new ViewHolder(view, this.buttonListener);
  }

  @Override
  public void onBindViewHolder(@NonNull final MyEventInvitesRecyclerViewAdapter.ViewHolder viewHolder,
      int i) {

    // Set values based on Event_withID Object from List
    viewHolder.eventWithID = this.eventInvites.get(i);
    viewHolder.eventID = this.eventInvites.get(i).getEventID();
    viewHolder.event = viewHolder.eventWithID.getEvent();


    // Set values based on event from Event_withID Object
    viewHolder.eventTitle.setText(viewHolder.event.getTitle());
    viewHolder.eventDate
        .setText(viewHolder.event.getEventDate() + " " + viewHolder.event.getEventTime());
    viewHolder.eventOrganizer.setText("Organizer: " + viewHolder.event.getOrganizer());
    viewHolder.eventLocation.setText(viewHolder.event.getLocation());

    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
                if (null != myListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    myListener.onListFragmentInteraction(viewHolder.eventWithID, v, "eventInvites");
                }
      }
    });
  }

  @Override
  public int getItemCount() {
    return this.eventInvites.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public WeakReference<AcceptRejectButtonListener> listener;
    public final View mView;

    public final TextView eventTitle;
    public final TextView eventDate;
    public final TextView eventOrganizer;
    public final TextView eventLocation;

    public final Button accept;
    public final Button reject;

    public Event_withID eventWithID;
    public Event event;
    public String eventID;

    public ViewHolder(@NonNull View itemView, AcceptRejectButtonListener listener) {
      super(itemView);
      this.mView = itemView;

      this.eventTitle = this.mView.findViewById(R.id.eventInviteTitle);
      this.eventDate = this.mView.findViewById(R.id.eventInviteDateTime);
      this.eventOrganizer = this.mView.findViewById(R.id.eventInviteInvitor);
      this.eventLocation = this.mView.findViewById(R.id.eventInviteLocation);

      this.accept = this.mView.findViewById(R.id.eventInviteAcceptButton);
      this.reject = this.mView.findViewById(R.id.eventInviteRejectButton);

      this.listener = new WeakReference<>(listener);

      this.accept.setOnClickListener(this);
      this.reject.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.eventInviteAcceptButton:
          this.listener.get().onAccept(getLayoutPosition());
          break;
        case R.id.eventInviteRejectButton:
          this.listener.get().onReject(getLayoutPosition());
          break;
        default:
          break;
      }
    }

  }
}
