package com.app.WeOut;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.WeOut.dummy.DummyContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.Event;
import utils.AcceptRejectButtonListener;
import utils.Event_withID;
import utils.MyEventInvitesRecyclerViewAdapter;
import utils.User;
import utils.Utilities;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainActivityNotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainActivityNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainActivityNotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Listeners
    private OnFragmentInteractionListener mListener;
    private OnListFragmentInteractionListener listListener;
    private AcceptRejectButtonListener acceptRejectListener;

    // Event invites array list and adapter
    private ArrayList<Event_withID> eventInvites;
    private MyEventInvitesRecyclerViewAdapter myAdapter;
    private TextView noEventInvitesTextView;

    // Firebase and debugging variables
    private FirebaseFirestore db;
    private String TAG;

    private TextView eventNotificationsHeader;

    public MainActivityNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainActivityNotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainActivityNotificationsFragment newInstance(String param1, String param2) {
        MainActivityNotificationsFragment fragment = new MainActivityNotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Handle event invites
        this.eventInvites = new ArrayList<>();
//        this.addDummyEventInvites();

        TAG = "EventInvites: ";

        // Instantiate db
        db = FirebaseFirestore.getInstance();

        this.acceptRejectListener = new AcceptRejectButtonListener() {
            @Override
            public void onAccept(int position) {
                System.out.println("Event Invite " + eventInvites.get(position).getEvent().getTitle() + " was accepted!");

                // Create Batch to write all your changes
                final WriteBatch batch = db.batch();

                // Variables to work with
                // Get event ID by position
                String eventID = eventInvites.get(position).getEventID();
                // Get username
                final String username = Utilities.getCurrentUsername();

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // Remove this eventID from current user's "events/invited" document
                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                DocumentReference df_users_username_events_invited = db
                        .collection("users").document(username)
                        .collection("events").document("invited");

                HashMap <String, Object> removeEventID_fromUserMap = new HashMap<>();
                removeEventID_fromUserMap.put(eventID, FieldValue.delete());

                batch.update(df_users_username_events_invited, removeEventID_fromUserMap);

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // Add this eventID to current user's "events/accepted" document
                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                DocumentReference df_users_username_events_accepted = db
                        .collection("users").document(username)
                        .collection("events").document("accepted");

                HashMap <String, Object> addEventID_toUserMap = new HashMap<>();
                addEventID_toUserMap.put(eventID, true);

                batch.set(df_users_username_events_accepted, addEventID_toUserMap, SetOptions.merge());

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // Remove this user's username from the "events/eventID/invitedMap"
                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                final DocumentReference df_events_eventID = db
                        .collection("events").document(eventID);

                HashMap <String, Object> removeUserFromEventID_invitedMap = new HashMap<>();
                removeUserFromEventID_invitedMap.put("invitedMap." + username, FieldValue.delete());

                batch.update(df_events_eventID, removeUserFromEventID_invitedMap);

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // Add this user's username to the "events/eventID/acceptedMap"
                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                final HashMap <String, Object> addUserToEventID_acceptedMap = new HashMap<>();
                // TODO: hashmapChanges

                // Get current user's full name
                DocumentReference df_currUser = db.collection("users").document(username);

                df_currUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists() && documentSnapshot != null) {

                            String currUserFullName = documentSnapshot.toObject(User.class).getFullName();

                            // Add one entry to the accepted map (the organizer AKA curr user)
                            addUserToEventID_acceptedMap.put("attendingMap." + username, currUserFullName);

                            Log.d(TAG, "Getting full name from current user was successful. [" +
                                    username + ", " + currUserFullName + "]");

                            batch.update(df_events_eventID, addUserToEventID_acceptedMap);

                            // Finished adding all the values to the batch. Now commit all this data.
                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Accept Event batch writing successful!");
                                    Utilities.displaySnackBar(getView(), getContext(), "Successfully accepted event!");
                                    myAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error writing batch: " + e.getMessage());
                                    Utilities.displaySnackBar(getView(), getContext(), "Error accepting event.");
                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure to get " + username + " 's full name. Error: " + e.getMessage());
//                        Utilities.displaySnackBar(view, getApplicationContext(), "Error retrieving user information.");
                        addUserToEventID_acceptedMap.put(username, "Error Name");
                    }
                });


            }

            @Override
            public void onReject(int position) {
                System.out.println("Event Invite " + eventInvites.get(position).getEvent().getTitle() + " was rejected!");

                // Create Batch to write all your changes
                WriteBatch batch = db.batch();

                // Variables to work with
                // Get event ID by position
                String eventID = eventInvites.get(position).getEventID();
                // Get username
                String username = Utilities.getCurrentUsername();

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // Remove this eventID from current user's "events/invited" document
                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                DocumentReference df_users_username_events_invited = db
                        .collection("users").document(username)
                        .collection("events").document("invited");

                HashMap <String, Object> removeEventID_fromUserMap = new HashMap<>();
                removeEventID_fromUserMap.put(eventID, FieldValue.delete());

                batch.update(df_users_username_events_invited, removeEventID_fromUserMap);

                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                // Remove this username from the "events/eventID/invitedMap"
                // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

                DocumentReference df_events_eventID = db
                        .collection("events").document(eventID);

                HashMap <String, Object> removeUserFromEventID_invitedMap = new HashMap<>();
                removeUserFromEventID_invitedMap.put("invitedMap." + username, FieldValue.delete());

                batch.update(df_events_eventID, removeUserFromEventID_invitedMap);

                // Finished adding all the values to the batch. Now commit all this data.
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Accept Event batch writing successful!");
                        Utilities.displaySnackBar(getView(), getContext(), "Success declining event.");
                        myAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing batch: " + e.getMessage());
                        Utilities.displaySnackBar(getView(), getContext(), "Error declining event.");
                    }
                });

//                eventInvites.remove(position);
//                myAdapter.notifyItemRemoved(position);
//                myAdapter.notifyItemRangeChanged(position, eventInvites.size());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_activity_notifications, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.eventInviteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get the text view to display when you dont have events
        this.noEventInvitesTextView = view.findViewById(R.id.TextView_NoInvites);
        this.noEventInvitesTextView.setVisibility(View.VISIBLE);

        // Create and set adapter
        this.myAdapter = new MyEventInvitesRecyclerViewAdapter(this.eventInvites, this.listListener, this.acceptRejectListener, this.noEventInvitesTextView);
        recyclerView.setAdapter(this.myAdapter);

        this.eventNotificationsHeader = view.findViewById(R.id.notificationTitle);
        this.eventNotificationsHeader.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.lobster));

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }

    private void addDummyEventInvites(){
        Event dummyEvent = new Event("Watch Spider-Man", "", "7/18/2019", "10:00 P.M.", "", "", "saif");
        this.eventInvites.add(new Event_withID(dummyEvent, "fakeID"));
    }
}
